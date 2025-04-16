package com.analistas.amesistema.amesistema.web.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.analistas.amesistema.amesistema.model.service.IPedidoService;
import com.analistas.amesistema.amesistema.model.service.IProductoService;

import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequestMapping("/pedidos")
public class PedidoController {

    @Autowired
    IProductoService productoService;

    @Autowired
    IPedidoService pedidoService;

    @GetMapping("/listado")
    public String lsitadoPedidos(Model model) {

        model.addAttribute("titulo", "Listado de Pedidos de compras Online");
        model.addAttribute("pedidos", pedidoService.buscarTodos());

        return "pedidos/list";
    }

}
