package com.analistas.amesistema.amesistema.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.analistas.amesistema.amesistema.model.domain.Compra;
import com.analistas.amesistema.amesistema.model.domain.LineaCompra;
import com.analistas.amesistema.amesistema.model.domain.Movimiento;
import com.analistas.amesistema.amesistema.model.domain.Producto;
import com.analistas.amesistema.amesistema.model.service.ICompraService;
import com.analistas.amesistema.amesistema.model.service.IMetodoPagoService;
import com.analistas.amesistema.amesistema.model.service.IMovimientoService;
import com.analistas.amesistema.amesistema.model.service.IProductoService;
import com.analistas.amesistema.amesistema.model.service.IProveedorService;
import com.analistas.amesistema.amesistema.model.service.IUsuarioService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequestMapping("/compras")
@SessionAttributes("compra")
@Secured({ "ROLE_ADMIN", "ROLE_CAJERO" })
public class CompraController {

    @Autowired
    IProductoService productoService;

    @Autowired
    IProveedorService proveedorService;

    @Autowired
    ICompraService compraService;

    @Autowired
    IUsuarioService usuarioService;

    @Autowired
    IMetodoPagoService metodoPagoService;

    @Autowired
    IMovimientoService movimientoService;

    @GetMapping("/listado")
    public String listadoCompras(Model model) {

        model.addAttribute("titulo", "Listado de Compras");
        model.addAttribute("compras", compraService.buscarTodos());

        return "compras/list";
    }

    @GetMapping("/nuevo")
    public String nuevaCompra(Model model) {

        model.addAttribute("titulo", "Nueva Compra");
        model.addAttribute("compra", new Compra());
        model.addAttribute("proveedores", proveedorService.buscarTodos());
        model.addAttribute("usuarios", usuarioService.buscarTodos());
        model.addAttribute("metodosPago", metodoPagoService.buscarTodos());

        return "compras/form2";
    }

    @PostMapping("/guardar")
    public String guardarCompra(@Valid Compra compra, BindingResult result,
            @RequestParam("item_id[]") List<String> itemIds, @RequestParam("cantidad_[]") List<String> cantidades,
            Model model, RedirectAttributes flash,
            SessionStatus status) {

        // Verificar si hay errores
        if (result.hasErrors()) {
            model.addAttribute("danger", "Corregir errores");
            return "compras/form2";
        }

        // Verificar si hay productos en la lista
        if (itemIds == null || itemIds.size() == 1) { // Tiene 1 fila por defecto
            model.addAttribute("titulo", "Nueva Compra");
            model.addAttribute("warning", "Añadir productos a la compra...");
            return "compras/form2";
        }

        String mensaje = (compra.getId() == null || compra.getId() == 0)
                ? "Compra añadida"
                : "Compra modificada";

        flash.addFlashAttribute((compra.getId() == null || compra.getId() == 0) ? "success" : "warning", mensaje);

        // Para manejar la creacion de movimiento si se edita o no
        boolean existe = compra.getId() != null;

        // Manejar líneas existentes (si se está editando una compra)
        if (existe) {
            Compra compraExistente = compraService.buscarPorId(compra.getId());
            if (compraExistente != null) {
                // Devolver el stock de los productos asociados a las lineas antiguas
                for (LineaCompra linea : compraExistente.getLineas()) {
                    Producto producto = linea.getProducto();
                    producto.setStock(producto.getStock() - linea.getCantidad());
                    productoService.guardar(producto);
                }

                // Limpiar las lineas antiguas:
                compraExistente.getLineas().clear();
                compraService.guardar(compraExistente);
            }
        }

        // Si no hay errores...
        LineaCompra lineaCompra;
        Producto producto;

        // Cargar las lineas en la nueva compra
        for (int i = 0; i < itemIds.size() - 1; i++) {
            lineaCompra = new LineaCompra();
            Long id = Long.parseLong(itemIds.get(i)); // pasa el id de la linea de compra a tipo Long
            int cant = Integer.parseInt((cantidades.get(i)));

            producto = productoService.buscarPorId(id);

            lineaCompra.setProducto(producto);
            lineaCompra.setCantidad(cant);

            producto.setStock(cant + producto.getStock());

            compra.addLinea(lineaCompra);
        }

        compraService.guardar(compra);
        status.setComplete();

        if (existe) {
            // Buscar el movimiento existente asociado a la compra
            Movimiento movimientoExistente = movimientoService.buscarPorCompra(compra);
            if (movimientoExistente != null) {
                // Actualizamos el movimiento existente para no crear otra linea
                movimientoExistente.setCompra(compra);
                movimientoService.guardar(movimientoExistente);
            }
        } else {
            // Esto es para guardar el movimiento por primera vez
            Movimiento movimiento = new Movimiento();
            movimiento.setCompra(compra);
            movimientoService.guardar(movimiento);
        }

        return "redirect:/compras/listado";
    }

    @GetMapping("/editar/{id}")
    public String editarCompra(@PathVariable Long id, Model model, RedirectAttributes flash) {

        Compra compra = compraService.buscarPorId(id);

        model.addAttribute("proveedores", proveedorService.buscarTodos());
        model.addAttribute("usuarios", usuarioService.buscarTodos());
        model.addAttribute("titulo", "Editar Compra");
        model.addAttribute("compra", compra);

        return "compras/form2";
    }

    @GetMapping("/borrar/{id}")
    public String borrarCompra(@PathVariable Long id, RedirectAttributes flash) {

        Compra compra = compraService.buscarPorId(id);

        if (compra != null) {
            // Devolver stock a las compras anuladas.
            if (compra.getId() != null) {
                Compra compraExistente = compraService.buscarPorId(compra.getId());
                if (compraExistente != null) {
                    // Devolver el stock de los productos asociados a las lineas antiguas
                    for (LineaCompra linea : compraExistente.getLineas()) {
                        Producto producto = linea.getProducto();
                        producto.setStock(producto.getStock() - linea.getCantidad());
                        productoService.guardar(producto);
                    }
                }
            }

            compraService.borrarPorId(id);
            String mensaje = "Compra eliminada";
            flash.addFlashAttribute("danger", mensaje);
        } else {
            String mensaje = "Compra no encontrada";
            flash.addFlashAttribute("danger", mensaje);
        }

        return "redirect:/compras/listado";
    }

    @GetMapping(value = "/buscar-productos/{criterio}", produces = { "application/json" })
    public @ResponseBody List<Producto> buscarProductos(@PathVariable("criterio") String texto) {
        return productoService.buscarPorCompra(texto);
    }
}
