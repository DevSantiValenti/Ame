package com.analistas.amesistema.amesistema.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.analistas.amesistema.amesistema.model.service.ICompraService;
import com.analistas.amesistema.amesistema.model.service.IMovimientoService;
import com.analistas.amesistema.amesistema.model.service.IVentaService;

@Controller
@RequestMapping("/movimientos")
public class MovimientoController {
    
    @Autowired
    ICompraService compraService;

    @Autowired
    IVentaService ventaService;

    @Autowired
    IMovimientoService movimientoService;


    @GetMapping("/listado")
    public String listado(Model model){

        model.addAttribute("titulo", "Movimientos de la Caja");
        model.addAttribute("movimientos", movimientoService.buscarTodos());

        return "movimientos/list";
    }
}
