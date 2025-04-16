package com.analistas.amesistema.amesistema.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.analistas.amesistema.amesistema.model.domain.Permiso;
import com.analistas.amesistema.amesistema.model.domain.Usuario;
import com.analistas.amesistema.amesistema.model.service.IUsuarioService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;






@Controller
@RequestMapping("/usuarios")
@Secured({"ROLE_ADMIN", "ROLE_CAJERO"})
public class UsuarioController {
    
    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    @Autowired
    IUsuarioService usuarioService;

    
    @GetMapping("/listado")
    public String listado(Model model) {

        model.addAttribute("titulo", "Listado de Usuarios");
        model.addAttribute("usuarios", usuarioService.buscarTodos()); //Esto hace que me traiga todos los usuarios que encuentre en la base de datos
        //"usuarios" pongo en el th:each, para que busque todos y los traiga en el list
        return "/usuarios/list";
    }
 
    @GetMapping("/nuevo")
    public String nuevoUsuario(Model model) {

        model.addAttribute("titulo", "Nuevo Usuario");
        model.addAttribute("usuario", new Usuario());

        return "/usuarios/form";
    }
    
    @PostMapping("/guardar")
    public String guardarUsuario(Model model, @Valid Usuario usuario, BindingResult result, RedirectAttributes flash, SessionStatus status) {

        if (result.hasErrors()) {
            model.addAttribute("danger", "Corregir errores");
            model.addAttribute("titulo", usuario.getId() == null ? "Nuevo Usuario" : "Editar Usuario");
        }

        String mensaje = (usuario.getId() == null || usuario.getId() == 0) 
                ? "Usuario " + usuario.getNombre() + " creado..."
                : "Usuario " + usuario.getNombre() + " modificado...";

        flash.addFlashAttribute((usuario.getId() == null || usuario.getId() == 0) ? "success" : "warning", mensaje);

        if (usuario.getId() == null || usuario.getId() == 0 || usuario.getId() != 0) {
            usuario.setActivo(true);
        }
        
        //Encriptar la contraseña:
        usuario.setClave(passwordEncoder.encode(usuario.getClave()));

        usuarioService.guardar(usuario);
        status.setComplete();

        return "redirect:/usuarios/listado";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable("id") Long id, Model model, RedirectAttributes flash) {  

        Usuario usuario = usuarioService.buscarPorId(id);

        model.addAttribute("titulo", "Editar Usuario");
        model.addAttribute("usuario", usuario);

        return "usuarios/form";
    }

    @GetMapping("/borrar/{id}")
    public String borrar(@PathVariable Long id, RedirectAttributes flash) {
        
        Usuario usuario = usuarioService.buscarPorId(id);

        if (usuario != null) {
            usuarioService.borrarPorId(id);
            String mensaje = "Usuario " + usuario.getNombre() + " eliminado.";
            flash.addFlashAttribute("danger", mensaje);
        } else {
            String mensaje = "Usuario no encontrado";
            flash.addFlashAttribute("danger", mensaje);
        }

        return "redirect:/usuarios/listado";
    }
    
    @GetMapping("/habdes/{id}")
    public String cambiarEstado(@PathVariable Long id, RedirectAttributes flash) {
        
        Usuario usuario = usuarioService.buscarPorId(id);
        usuario.setActivo(!usuario.isActivo());
        usuarioService.guardar(usuario);

        String mensaje = usuario.isActivo() ? "Usuario " + usuario.getNombre() + " habilitado" : "Usuario " + usuario.getNombre() + " deshabilitado";
        flash.addFlashAttribute(usuario.isActivo() ? "info" : "danger", mensaje);

        return "redirect:/usuarios/listado";
    }
    
    @ModelAttribute("permisos")
    public List<Permiso> listarPermisos() {
        return usuarioService.getPermisos();
    }
    
}
