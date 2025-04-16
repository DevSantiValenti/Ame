package com.analistas.amesistema.amesistema.web.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.analistas.amesistema.amesistema.model.domain.Cliente;
import com.analistas.amesistema.amesistema.model.domain.Usuario;
import com.analistas.amesistema.amesistema.model.service.ClienteServiceImpl;
import com.analistas.amesistema.amesistema.model.service.UsuarioServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private ClienteServiceImpl clienteService;

    @Autowired
    private UsuarioServiceImpl usuarioService;

    @ModelAttribute
    public void addUserInfo(Model model) {
        String username = getLoggedUsername();

        if (username != null) {
            Cliente cliente = clienteService.findByDni(username);
            Usuario usuario = usuarioService.findByNombre(username);

            if (cliente != null) {
                model.addAttribute("nombreUsuario", cliente.getNombre());
                model.addAttribute("idCliente", cliente.getId());
                model.addAttribute("tipoUsuario", "cliente");
            } else if (usuario != null) {
                model.addAttribute("nombreUsuario", usuario.getNombre());
                model.addAttribute("idUsuario", usuario.getId());
                model.addAttribute("tipoUsuario", "usuario");
            }
        }
    }

    private String getLoggedUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails) {
                return ((UserDetails) principal).getUsername();
            }
        }
        return null;
    }
}

