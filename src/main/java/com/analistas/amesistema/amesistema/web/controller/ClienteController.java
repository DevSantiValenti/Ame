package com.analistas.amesistema.amesistema.web.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.analistas.amesistema.amesistema.model.domain.Cliente;
import com.analistas.amesistema.amesistema.model.domain.Permiso;
import com.analistas.amesistema.amesistema.model.repository.IClienteRepository;
import com.analistas.amesistema.amesistema.model.service.IClienteService;
import com.analistas.amesistema.amesistema.model.service.IPermisoService;

import org.springframework.ui.Model;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequestMapping("/clientes")
@SessionAttributes("cliente") // Asegura que el objeto se mantenga entre el Get y el Post
public class ClienteController {

    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    @Autowired
    IPermisoService permisoService;

    @Autowired
    IClienteService clienteService;

    @Autowired
    IClienteRepository clienteRepository;

    @GetMapping("/nuevo")
    public String nuevoUsuario(Model model) {

        Cliente cliente = new Cliente();
        Permiso permisoCliente = permisoService.buscarPorId(4L);
        cliente.setPermiso(permisoCliente);

        model.addAttribute("titulo", "Crear Cuenta");
        model.addAttribute("cliente", new Cliente());

        return "/register-form";
    }

    // @Secured("ROLE_ADMIN")
    @PostMapping("/guardar")
    public String guardarCliente(@Valid Cliente cliente, BindingResult result, Model model, RedirectAttributes flash,
            SessionStatus status) {

        // Verificar si hay errores
        if (result.hasErrors()) {
            model.addAttribute("danger", "Completar los campos restantes...");
            // System.out.println(result.getAllErrors());
            return "register-form";
        }

        boolean existe = cliente.getId() == null || cliente.getId() == 0;

        if (cliente.getId() == null || cliente.getId() == 0) {
            // Verificar si el DNI ya existe
            if (clienteService.existeDni(cliente.getDni())) {
                model.addAttribute("danger", "El DNI ingresado ya existe.");
                return "register-form";
            }
        }

        String mensaje = (cliente.getId() == null || cliente.getId() == 0)
                ? cliente.getNombre() + " ha creado su cuenta satisfactoriamente "
                : cliente.getNombre() + " modificado satisfactoriamente";

        // Encriptar la contraseña:
        cliente.setClave(passwordEncoder.encode(cliente.getClave()));

        cliente.setFechaRegistro(LocalDateTime.now());

        // Aca asignamos el rol de cliente:
        Permiso permisoCliente = permisoService.buscarPorId(4L);
        cliente.setPermiso(permisoCliente);

        flash.addFlashAttribute((cliente.getId() == null || cliente.getId() == 0) ? "success" : "warning", mensaje);

        clienteService.guardar(cliente);
        status.setComplete();

        // Verificamos si el usuario es un cliente y lo redirigimos al home
        org.springframework.security.core.Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails) {
                String username = ((UserDetails) principal).getUsername();

                // Asumimos que un cliente tiene un rol específico, por ejemplo, "ROLE_CLIENTE"
                if (username != null && authentication.getAuthorities().stream()
                        .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_CLIENTE"))) {
                    return "redirect:/home"; // Redirigir a home si es un cliente
                }
            }
        }

        return existe ? "redirect:/home" : "redirect:/clientes/listado";
    }

    @Secured({"ROLE_ADMIN", "ROLE_CLIENTE"})
    @GetMapping("/editar/{id}")
    public String editarCliente(@PathVariable Long id, Model model, RedirectAttributes flash) {

        Cliente cliente = clienteService.buscarPorId(id);

        model.addAttribute("titulo", "Editar Datos Cliente");
        model.addAttribute("cliente", cliente);

        return "clientes/form";
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/listado")
    public String listadoUsuarios(Model model) {

        model.addAttribute("titulo", "Listado de Clientes");
        model.addAttribute("clientes", clienteService.buscarTodos());

        return "clientes/list";
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/habdeshab/{id}")
    public String habDeshabCliente(@PathVariable Long id, RedirectAttributes flash) {

        Cliente cliente = clienteService.buscarPorId(id);
        cliente.setActivo(!cliente.isActivo());
        clienteService.guardar(cliente);

        String mensaje = cliente.isActivo()
                ? "Cliente " + cliente.getNombre() + " habilitado"
                : "Cliente " + cliente.getNombre() + " deshabilitado";

        flash.addFlashAttribute(cliente.isActivo() ? "info" : "danger", mensaje);

        return "redirect:/clientes/listado";
    }

    @GetMapping("/search-dni")
    @ResponseBody
    public List<Cliente> searchDNI(@RequestParam String dni) {
        return clienteRepository.findByDniStartingWith(dni);
    }

}
