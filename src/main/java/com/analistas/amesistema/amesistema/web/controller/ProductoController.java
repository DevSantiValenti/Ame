package com.analistas.amesistema.amesistema.web.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.analistas.amesistema.amesistema.model.domain.Categoria;
import com.analistas.amesistema.amesistema.model.domain.Producto;
import com.analistas.amesistema.amesistema.model.service.ICategoriaService;
import com.analistas.amesistema.amesistema.model.service.IProductoService;

import jakarta.validation.Valid;






@Controller
@RequestMapping("/productos")
@SessionAttributes({"producto", "categoria"})
public class ProductoController {

    @Autowired
    IProductoService productoService;

    @Autowired
    ICategoriaService categoriaService;
    
    
    @GetMapping("/listado")
    public String listado(@RequestParam(value = "categoria", required = false) Long cateId ,@RequestParam(value = "color", required = false) String color ,Model model) {

        List<Producto> productos;
        
        if (color != null && !color.isEmpty()) {
            //Filtrar por coincidencias en el color
            productos = productoService.buscarPorColor(color);
        } else if (cateId != null) {
            //Filtrar productos por categoria
            productos = productoService.buscarPorCategoria(cateId);
        } else {
            //Mostrar todos los productos
            productos = productoService.buscarTodo();
        }


        model.addAttribute("titulo", "Listado de Productos");
        // model.addAttribute("productos", productoService.buscarTodo()); 
        model.addAttribute("productos", productos);
        model.addAttribute("categorias", productoService.getCategorias());
        model.addAttribute("colores", obtenerColoresPredefinidos());

        

        return "productos/list";
    }

    private List<String> obtenerColoresPredefinidos() {
        return Arrays.asList("Rojo", "Azul", "Negro", "Blanco", "Verde", "Gris", "Marrón", "Rosado", "Morado", "Naranja", "Púrpura", "Celeste", "Amarillo");
    }

    @Secured({"ROLE_ADMIN", "ROLE_REPOSITOR", "ROLE_CAJERO"})
    @GetMapping("/listadoAdmin")
    public String listadoAdmin(Model model) {

        List<Producto> productos = productoService.buscarTodo();
        boolean hayBajoStock = productos.stream().anyMatch(p -> p.getStock() <= 6);
        String mensajeStock = hayBajoStock ? "⚠️ Atención: Algunos productos tienen stock bajo (6 unidades o menos)." : "";

        model.addAttribute("titulo", "Listado de Productos");
        model.addAttribute("productos", productoService.buscarTodo()); 
        model.addAttribute("mensajeStock", mensajeStock);

        return "productos/list-admin";
    }

    @Secured({"ROLE_ADMIN", "ROLE_REPOSITOR", "ROLE_CAJERO"})
    @GetMapping("/nuevo")
    public String nuevo(Model model) {

        model.addAttribute("titulo", "Nuevo Producto");
        model.addAttribute("producto", new Producto());

        return "productos/form";
    }

    @Secured({"ROLE_ADMIN", "ROLE_REPOSITOR", "ROLE_CAJERO"})
    @PostMapping("/guardar")
    public String guardar(@Valid Producto producto, BindingResult result, Model model, RedirectAttributes flash, SessionStatus status) {
        
        //Verificar si hay errores...
        if (result.hasErrors()) {
            model.addAttribute("danger", "Corregir errores");
        }

        String mensaje = (producto.getId() == null || producto.getId() == 0)
                 ? "Producto " + producto.getDescripcion() + " añadido"
                 : "Producto " + producto.getDescripcion() + " modificado";

        flash.addFlashAttribute((producto.getId() == null || producto.getId() == 0) ? "success" : "warning", mensaje);

        productoService.guardar(producto);
        status.setComplete(); //Lo que hace es limpiar la variable "producto" definida en el SessionStatus de arriba

        return "redirect:/productos/listadoAdmin";
    }

    

    @Secured({"ROLE_ADMIN", "ROLE_REPOSITOR", "ROLE_CAJERO"})
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable("id") Long id, Model model, RedirectAttributes flash) {

        Producto producto = productoService.buscarPorId(id);

        model.addAttribute("titulo", "Editar Producto");
        model.addAttribute("producto", producto);

        return "productos/form";
    }

    @Secured({"ROLE_ADMIN", "ROLE_REPOSITOR", "ROLE_CAJERO"})
    @GetMapping("/borrar/{id}")
    public String cambiarEstado(@PathVariable Long id, RedirectAttributes flash) {
        
        Producto producto = productoService.buscarPorId(id);
        producto.setActivo(!producto.isActivo()); 
        productoService.guardar(producto);

        String mensaje = producto.isActivo() ? "Producto " + producto.getDescripcion() + " habilitado" : "Producto " + producto.getDescripcion() + " deshabilitado";
        flash.addFlashAttribute(producto.isActivo() ? "info" : "danger", mensaje);
        return "redirect:/productos/listadoAdmin";
        
    }

    @GetMapping("/borrarC/{id}")
    public String borrarProducto(@PathVariable Long id, RedirectAttributes flash) {
        
        Producto producto = productoService.buscarPorId(id);
        if (producto != null) {
            productoService.borrarPorId(id);
            String mensaje = "Producto " + producto.getDescripcion() + " borrado";
            flash.addFlashAttribute("danger", mensaje); 
        } else {
            String mensaje = "Producto no encontrado...";
            flash.addFlashAttribute("danger",mensaje);
        }
        
        return "redirect:/productos/listadoAdmin";
    }
    

    
    
    

    // CATEGORIA ----------------------------------
    @Secured({"ROLE_ADMIN", "ROLE_CAJERO"})
    @GetMapping("/nuevaCat")
    public String nuevaCategoria(Model model) {

        model.addAttribute("titulo", "Agregar categoría");
        model.addAttribute("categoria", new Categoria());

        return "productos/cat-form";
    }

    @Secured({"ROLE_ADMIN", "ROLE_CAJERO"})
    @GetMapping("/listadoCat")
    public String listadoCat(Model model) {

        model.addAttribute("titulo", "Listado de Categorias");
        model.addAttribute("categoria", productoService.getCategorias());

        return "productos/list-cat";
    }

    @Secured({"ROLE_ADMIN", "ROLE_CAJERO"})
    @PostMapping("/guardarCat")
    public String guardarCategoria(@Valid Categoria categoria, BindingResult result, Model model, RedirectAttributes flash, SessionStatus status) {
        
        //Verificar si hay errores
        if (result.hasErrors()) {
            model.addAttribute("danger", "Corregir errores");
        }

        String mensaje = (categoria.getId() == null || categoria.getId() == 0)
                        ? "Categoria " + categoria.getNombre() + " añadido"
                        : "Categoria " + categoria.getNombre() + " modificado";

        flash.addFlashAttribute((categoria.getId() == null || categoria.getId() == 0) ? "success" : "warning", mensaje);

        categoriaService.guardar(categoria);
        status.setComplete();

        return "redirect:/productos/listadoCat";
    }

    @Secured({"ROLE_ADMIN", "ROLE_CAJERO"})
    @GetMapping("/editarCat/{id}")
    public String editarCategoria(@PathVariable("id") Long id, Model model, RedirectAttributes flash) {
        
        Categoria categoria = categoriaService.buscarPorId(id);

        model.addAttribute("titulo", "Editar Categoria");
        model.addAttribute("categoria", categoria);
        
        return "productos/cat-form";
    }

    @Secured({"ROLE_ADMIN", "ROLE_CAJERO"})
    @GetMapping("/borrarCat/{id}")
    public String borrarCategoria(@PathVariable Long id, RedirectAttributes flash) {
        
        Categoria categoria = categoriaService.buscarPorId(id);
        if (categoria != null) {
            categoriaService.borrarPorId(id);
            String mensaje = "Usuario " + categoria.getNombre() + " eliminado.";
            flash.addFlashAttribute("danger", mensaje);
        }
        // categoriaService.borrarPorId(id);
        

        return "redirect:/productos/listadoCat";
    }


    @ModelAttribute("categorias")
    public List<Categoria> listarCategorias() {
        return productoService.getCategorias();
    }
    
    
    
}
