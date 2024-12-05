package com.trilce.Bibloteca.controladores;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.trilce.Bibloteca.Entidades.Usuario;
import com.trilce.Bibloteca.repositorio.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class RegistroController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/registrarme")
    public String mostrarFormularioRegistro() {
        return "registrarme";
    }

    @PostMapping("/registrarme")
    public String registrarUsuario(Usuario usuario, String confirmarContraseña, Model model) {
        // Validar que el correo sea institucional
        if (!usuario.getCorreo().endsWith("@trilce.edu")) {
            model.addAttribute("error", "El correo debe ser institucional (@trilce.edu).");
            return "registrarme";
        }

        // Verificar si el correo ya está registrado
        if (usuarioRepository.findByCorreo(usuario.getCorreo()).isPresent()) {
            model.addAttribute("error", "El correo ya está registrado.");
            return "registrarme";
        }

        // Verificar si el DNI tiene exactamente 8 caracteres numéricos
        if (usuario.getDni().length() != 8 || !usuario.getDni().matches("\\d{8}")) {
            model.addAttribute("error", "El DNI debe tener exactamente 8 dígitos numéricos.");
            return "registrarme";
        }

        // Verificar si el DNI ya está registrado
        if (usuarioRepository.findByDni(usuario.getDni()).isPresent()) {
            model.addAttribute("error", "El DNI ya está registrado.");
            return "registrarme";
        }

        // Validar que las contraseñas coincidan
        if (!usuario.getContraseña().equals(confirmarContraseña)) {
            model.addAttribute("error", "Las contraseñas no coinciden.");
            return "registrarme";
        }

        // Encriptar la contraseña
        usuario.setContraseña(passwordEncoder.encode(usuario.getContraseña()));
        usuario.setRol(Usuario.Rol.LECTOR);
        usuarioRepository.save(usuario);

        model.addAttribute("mensaje", "Registro exitoso. Ahora puedes iniciar sesión.");
        return "redirect:/login";
    }
}
