package com.analistas.amesistema.amesistema.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.analistas.amesistema.amesistema.model.service.IClienteService;
import com.analistas.amesistema.amesistema.model.service.IUsuarioService;

@Controller
public class HomeController {

    @Autowired
    IUsuarioService usuarioService;

    @Autowired
    IClienteService clienteService;


    @GetMapping({ "/home", "/" })
    public String index(Model model) {

        model.addAttribute("titulo", "Amé");

        return "home/home";
    }


    @GetMapping("/PRUEBA")
    public String getMethodName(Model model) {

        model.addAttribute("cliente", clienteService.buscarTodos());

        return "PRUEBA";
    }

}
