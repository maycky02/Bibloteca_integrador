package com.trilce.Bibloteca.seguridad;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.trilce.Bibloteca.Entidades.Usuario;
import com.trilce.Bibloteca.repositorio.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (usuarioRepository.findByCorreo("admin@trilce.edu").isEmpty()) {
            Usuario admin = new Usuario();
            admin.setCorreo("admin@trilce.edu");
            admin.setContraseña(passwordEncoder.encode("123"));
            admin.setRol(Usuario.Rol.valueOf("ADMINISTRADOR"));
            admin.setDni("12345678");  // Asignar un valor para el DNI
            usuarioRepository.save(admin);
        }
    }

}
