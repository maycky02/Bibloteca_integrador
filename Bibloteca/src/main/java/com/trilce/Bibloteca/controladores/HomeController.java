package com.trilce.Bibloteca.controladores;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/inicio")
    public String redireccionSegunRol() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // Verifica si el usuario tiene el rol de Administrador
        if (auth.getAuthorities().stream().anyMatch(role -> role.getAuthority().equals("ADMINISTRADOR"))) {
            return "redirect:/admin/dashboard";  // Página de inicio del admin
        } else {
            return "redirect:/lector/catalogo";  // Página del catálogo para el lector
        }
    }
}
