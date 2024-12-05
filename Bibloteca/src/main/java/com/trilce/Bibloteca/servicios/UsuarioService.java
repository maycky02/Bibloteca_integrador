package com.trilce.Bibloteca.servicios;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.trilce.Bibloteca.Entidades.Usuario;
import com.trilce.Bibloteca.repositorio.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    /**
     * Verifica si un correo es válido.
     * 
     * @param correo El correo a validar.
     * @return true si el correo es institucional, false en caso contrario.
     */
    public boolean correoEsValido(String correo) {
        return correo.endsWith("@trilce.edu");
    }

    /**
     * Verifica si un DNI es único en el sistema.
     * 
     * @param dni El DNI a verificar.
     * @return true si el DNI no está registrado, false en caso contrario.
     */
    public boolean dniEsUnico(String dni) {
        return usuarioRepository.findByDni(dni).isEmpty();
    }

    /**
     * Registra un nuevo usuario en el sistema.
     * 
     * @param usuario El objeto Usuario que contiene los datos del usuario a registrar.
     * @return El usuario registrado.
     * @throws IllegalArgumentException Si el correo no es institucional o el DNI ya está registrado.
     */
    public Usuario registrarUsuario(Usuario usuario) {
        if (!correoEsValido(usuario.getCorreo())) {
            throw new IllegalArgumentException("El correo no es institucional.");
        }
        if (!dniEsUnico(usuario.getDni())) {
            throw new IllegalArgumentException("El DNI ya está registrado.");
        }
        return usuarioRepository.save(usuario);
    }

    /**
     * Busca un usuario por su ID.
     * 
     * @param id El ID del usuario a buscar.
     * @return El usuario encontrado.
     * @throws IllegalArgumentException Si no se encuentra un usuario con el ID especificado.
     */
    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
    }

    /**
     * Busca un usuario por su DNI.
     * 
     * @param dni El DNI del usuario a buscar.
     * @return El usuario encontrado.
     * @throws IllegalArgumentException Si no se encuentra un usuario con el DNI especificado.
     */
    public Usuario buscarPorDni(String dni) {
        Optional<Usuario> usuario = usuarioRepository.findByDni(dni);
        if (usuario.isPresent()) {
            return usuario.get();
        } else {
            throw new IllegalArgumentException("Usuario no encontrado con DNI: " + dni);
        }
    }

    /**
     * Obtiene un usuario por su ID.
     * 
     * @param id El ID del usuario a obtener.
     * @return El usuario encontrado.
     * @throws IllegalArgumentException Si no se encuentra un usuario con el ID especificado.
     */
    public Usuario obtenerUsuarioPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con id: " + id));
    }
}
