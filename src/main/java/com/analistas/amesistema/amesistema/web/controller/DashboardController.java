package com.analistas.amesistema.amesistema.web.controller;

import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;


@Controller
@RequestMapping("/dashboard")
public class DashboardController {
    
    @Secured({"ROLE_ADMIN", "ROLE_REPOSITOR", "ROLE_CAJERO"})
    @GetMapping("/inicio")
    public String inicioDashboard(Model model) {

        model.addAttribute("titulo", "Dashboard");

        return "dashboard";
    }
    

}
