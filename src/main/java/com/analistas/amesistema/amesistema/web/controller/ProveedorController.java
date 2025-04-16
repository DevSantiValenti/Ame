package com.analistas.amesistema.amesistema.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.analistas.amesistema.amesistema.model.domain.Proveedor;
import com.analistas.amesistema.amesistema.model.service.IProveedorService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;






@Controller
@RequestMapping("/proveedores")
@Secured("ROLE_ADMIN")
@SessionAttributes("proveedor")
public class ProveedorController {

    @Autowired
    IProveedorService proveedorService;

    @GetMapping("/nuevo")
    public String nuevoProveedor(Model model) {

        model.addAttribute("titulo", "Nuevo proveedor");
        model.addAttribute("proveedor", new Proveedor());

        return "proveedores/form";
    }
    

    @GetMapping("/listado")
    public String listadoProveedores(Model model) {
        
        model.addAttribute("titulo", "Lista de Proveedores");
        model.addAttribute("proveedores", proveedorService.buscarTodos());

        return "proveedores/list";
    }

    @PostMapping("/guardar")
    public String guardarProveedor(@Valid Proveedor proveedor, BindingResult result, Model model, RedirectAttributes flash, SessionStatus status) {

        //Verificar si hay errores
        if (result.hasErrors()) {
            model.addAttribute("danger", "Rellenar campos...");
        }

        String mensaje = (proveedor.getId() == null || proveedor.getId() == 0)
                        ? "Proveedor " + proveedor.getNombre() + " creado."
                        : "Proveedor " + proveedor.getNombre() + " modificado";

        flash.addFlashAttribute((proveedor.getId() == null || proveedor.getId() == 0) ? "success" : "warning", mensaje);

        proveedorService.guardar(proveedor);
        status.setComplete();
        
        return "redirect:/proveedores/listado";
    }

    @GetMapping("/editar/{id}")
    public String editarProveedor(@PathVariable Long id, Model model, RedirectAttributes flash) {

        Proveedor proveedor = proveedorService.buscarPorId(id);

        model.addAttribute("titulo", "Editar Proveedor");
        model.addAttribute("proveedor", proveedor);

        return "proveedores/form";
    }

    @GetMapping("/borrar/{id}")
    public String borrarProveedor(@PathVariable Long id, Model model, RedirectAttributes flash) {
        
        Proveedor proveedor = proveedorService.buscarPorId(id);

        if (proveedor != null) {
            proveedorService.borrarPorId(id);
            String mensaje = "Proveedor" + proveedor.getNombre() + " borrado";
            flash.addFlashAttribute("danger", mensaje);
        } else {
            String mensaje = "Proveedor no encontrado...";
            flash.addFlashAttribute("danger", mensaje);
        }
        
        return "redirect:/proveedores/listado";
    }
    
    
    
    

}
