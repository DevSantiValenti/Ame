package com.analistas.amesistema.amesistema.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.analistas.amesistema.amesistema.model.domain.Cliente;
import com.analistas.amesistema.amesistema.model.domain.LineaVenta;
import com.analistas.amesistema.amesistema.model.domain.MetodoPago;
import com.analistas.amesistema.amesistema.model.domain.Movimiento;
import com.analistas.amesistema.amesistema.model.domain.Pedido;
import com.analistas.amesistema.amesistema.model.domain.Producto;
import com.analistas.amesistema.amesistema.model.domain.Usuario;
import com.analistas.amesistema.amesistema.model.domain.Venta;
import com.analistas.amesistema.amesistema.model.service.IClienteService;
import com.analistas.amesistema.amesistema.model.service.IMetodoPagoService;
import com.analistas.amesistema.amesistema.model.service.IMovimientoService;
import com.analistas.amesistema.amesistema.model.service.IPedidoService;
import com.analistas.amesistema.amesistema.model.service.IProductoService;
import com.analistas.amesistema.amesistema.model.service.IUsuarioService;
import com.analistas.amesistema.amesistema.model.service.IVentaService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/ventas")
@SessionAttributes("venta")
@Secured({ "ROLE_ADMIN", "ROLE_CAJERO" })
public class VentaController {

    @Autowired
    IClienteService clienteService;

    @Autowired
    IMetodoPagoService metodoPagoService;

    @Autowired
    IUsuarioService usuarioService;

    @Autowired
    IVentaService ventaService;

    @Autowired
    IProductoService productoService;

    @Autowired
    IMovimientoService movimientoService;

    @Autowired
    IPedidoService pedidoService;

    @GetMapping("/listado")
    public String listadoCliente(Model model) {

        model.addAttribute("titulo", "Listado de Ventas");
        model.addAttribute("ventas", ventaService.buscarTodo());
        return "ventas/list";
    }

    @GetMapping("/nuevo")
    public String nuevaVenta(Model model) {

        model.addAttribute("titulo", "Nueva Venta");
        model.addAttribute("venta", new Venta());
        model.addAttribute("usuario", usuarioService.buscarTodos());
        model.addAttribute("metodoPago", metodoPagoService.buscarTodos());

        return "/ventas/form";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid Venta venta, BindingResult result, @RequestParam("item_id[]") List<String> itemIds,
            @RequestParam("cantidad_[]") List<String> cantidades, Model model, RedirectAttributes flash,
            SessionStatus status) {
        // Verificar si no hay errores
        if (result.hasErrors()) {
            model.addAttribute("danger", "Corrija los errores...");
            return "ventas/form";
        }

        // Verificar si hay productos en la lista...
        if (itemIds == null || itemIds.size() == 1) { // tiene 1 fila por defecto
            model.addAttribute("titulo", "Nueva Venta");
            model.addAttribute("warning", "Añadir productos a la venta...");
            return "ventas/form";
        }

        String mensaje = (venta.getId() == null || venta.getId() == 0)
                ? "Compra añadida"
                : "Compra modificada";

        flash.addFlashAttribute((venta.getId() == null || venta.getId() == 0) ? "success" : "warning", mensaje);

        // Para manejar la creacion de movimiento si se edita o no:
        boolean existe = venta.getId() != null;

        // Manejar líneas existentes (si se está editando una compra)
        if (existe) {
            Venta ventaExistente = ventaService.buscarPorId(venta.getId());
            if (ventaExistente != null) {
                // Devolver el stock de los productos asociados a las lineas antiguas
                for (LineaVenta linea : ventaExistente.getLineas()) {
                    Producto producto = linea.getProducto();
                    producto.setStock(producto.getStock() + linea.getCantidad());
                    productoService.guardar(producto);
                }

                // Limpiar las lineas antiguas:
                ventaExistente.getLineas().clear();
                ventaService.guardar(ventaExistente);
            }
        }

        // Si no hay errores...
        LineaVenta lineaVenta; // Instanciamos esto
        Producto producto;

        // Cargar las lineas en la nueva venta...
        for (int i = 0; i < itemIds.size() - 1; i++) {

            lineaVenta = new LineaVenta();
            Long id = Long.parseLong(itemIds.get(i)); // pasa el id de la linea de la venta a tipo Long
            int cant = Integer.parseInt((cantidades.get(i)));

            producto = productoService.buscarPorId(id);

            lineaVenta.setProducto(producto);
            lineaVenta.setPrecioActual(producto.getPrecioMin());
            lineaVenta.setCantidad(cant);

            // Verificar de nuevo si hay stock
            if (cant > producto.getStock()) {
                model.addAttribute("titulo", "Nueva Venta");
                model.addAttribute("warning", "No hay stock suficiente...");
                return "ventas/form";
            }

            // Importante: actualizar stock del producto...
            producto.setStock(producto.getStock() - cant);

            venta.addLinea(lineaVenta);
        }

        ventaService.guardar(venta);
        status.setComplete();

        // Aca se guarda el movimiento
        if (existe) {
            // Buscar el movimiento existente asociado a la venta
            Movimiento movimientoExistente = movimientoService.buscarPorVenta(venta);
            if (movimientoExistente != null) {
                movimientoExistente.setVenta(venta);
                movimientoService.guardar(movimientoExistente);
            }
        } else {
            // Esto es para guardar el movimiento una vez realizada la venta
            Movimiento movimiento = new Movimiento();
            movimiento.setVenta(venta);
            movimientoService.guardar(movimiento);
        }

        return "redirect:/ventas/listado"; // Podemos retornar un listado de ventas a futuro
    }

    @GetMapping("/borrar/{id}")
    public String borrarVenta(@PathVariable Long id, Model model, RedirectAttributes flash) {
        Venta venta = ventaService.buscarPorId(id);

        Pedido pedido = pedidoService.buscarPorVentaId(id);
        if (pedido != null) {
            pedidoService.borrar(pedido);
        }

        if (venta != null) {
            if (venta.getId() != null) {
                Venta ventaExistente = ventaService.buscarPorId(venta.getId());
                if (ventaExistente != null) {
                    // Devolver el stock de los productos asociados a las lineas antiguas
                    for (LineaVenta linea : ventaExistente.getLineas()) {
                        Producto producto = linea.getProducto();
                        producto.setStock(producto.getStock() + linea.getCantidad());
                        productoService.guardar(producto);
                    }
                }
            }

            ventaService.borrarPorId(id);
            String mensaje = "Venta eliminada";
            flash.addFlashAttribute("danger", mensaje);
        }

        return "redirect:/ventas/listado";
    }

    @GetMapping("/editar/{id}")
    public String getMethodName(@PathVariable Long id, Model model, RedirectAttributes flash) {

        Venta venta = ventaService.buscarPorId(id);

        model.addAttribute("titulo", "Editar Venta");
        model.addAttribute("venta", venta);

        return "ventas/form";
    }

    // Metodo que responde a una peticion AJAX

    @GetMapping(value = "/buscar-productos/{criterio}", produces = { "application/json" })
    public @ResponseBody List<Producto> buscarProductos(@PathVariable("criterio") String texto) {
        return productoService.buscarPorVenta(texto);
    }

    @ModelAttribute("usuario")
    public List<Usuario> listarUsuarios() {
        return usuarioService.buscarTodos();
    }

    @ModelAttribute("metodoPago")
    public List<MetodoPago> listarPagos() {
        return metodoPagoService.buscarTodos();
    }

    @ModelAttribute("cliente")
    public List<Cliente> listarClientes() {
        return clienteService.buscarTodos();
    }

}
