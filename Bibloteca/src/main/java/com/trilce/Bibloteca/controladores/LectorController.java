package com.trilce.Bibloteca.controladores;

import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.trilce.Bibloteca.Entidades.Libro;
import com.trilce.Bibloteca.Entidades.Prestamo;
import com.trilce.Bibloteca.Entidades.Reserva;
import com.trilce.Bibloteca.Entidades.Sancion;
import com.trilce.Bibloteca.Entidades.Usuario;
import com.trilce.Bibloteca.repositorio.UsuarioRepository;
import com.trilce.Bibloteca.servicios.LibroService;
import com.trilce.Bibloteca.servicios.PrestamoService;
import com.trilce.Bibloteca.servicios.ReservaService;
import com.trilce.Bibloteca.servicios.SancionService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/lector")
@RequiredArgsConstructor
@Slf4j
public class LectorController {

    private final LibroService libroService;
    private final ReservaService reservaService;
    private final UsuarioRepository usuarioRepository;
    private final SancionService sancionService;
    private final PrestamoService prestamoService;

    // Método para verificar si el usuario está sancionado
    private boolean verificarSancion(Usuario usuario, Model model) {
        Long usuarioId = usuario.getId();

        // Obtener la sanción activa para el usuario
        Sancion sancion = sancionService.obtenerSancionActiva(usuarioId);

        if (sancion != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String mensaje = "No puedes realizar una reserva hasta el " + sancion.getFechaFin().format(formatter);
            model.addAttribute("sancionMensaje", mensaje);
            log.warn("Usuario con ID {} está sancionado hasta el {}", usuarioId, sancion.getFechaFin().format(formatter));
            return true; // Usuario sancionado
        }

        log.info("Usuario con ID {} no tiene sanción activa", usuarioId);
        return false; // Usuario no sancionado
    }

    // Método para listar el catálogo de libros
    @GetMapping("/catalogo")
    public String listarCatalogo(Model model) {
        List<Libro> libros = libroService.listarLibros();
        model.addAttribute("libros", libros);

        // Obtener el correo del usuario logueado
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String correoUsuario = user.getUsername();

        // Buscar al usuario por correo
        Usuario usuario = usuarioRepository.findByCorreo(correoUsuario)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        // Verificar si el usuario está sancionado
        if (verificarSancion(usuario, model)) {
            // Si está sancionado, retornar a la vista con el mensaje de sanción
            log.info("El usuario con correo {} está sancionado, no puede acceder al catálogo", correoUsuario);
            return "lector/catalogo";
        }

        log.info("El usuario con correo {} ha accedido al catálogo de libros", correoUsuario);
        return "lector/catalogo"; // Retornar la vista 'catalogo.html' si el usuario no está sancionado
    }

    // Método para reservar un libro
    @GetMapping("/reservar/{id}")
    public String reservarLibro(@PathVariable Long id, Model model) {
        // Obtener el libro por su ID
        Libro libro = libroService.buscarLibroPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Libro no encontrado"));

        // Obtener el correo del usuario logueado
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String correoUsuario = user.getUsername();

        // Buscar al usuario por correo
        Usuario usuario = usuarioRepository.findByCorreo(correoUsuario)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        // Verificar si el usuario está sancionado
        if (verificarSancion(usuario, model)) {
            // Si está sancionado, no permitir realizar la reserva
            log.warn("El usuario con correo {} no puede realizar la reserva debido a una sanción activa", correoUsuario);
            return "lector/catalogo"; // Redirigir a catálogo con el mensaje de sanción
        }

        // Si no está sancionado, proceder con la reserva
        Reserva nuevaReserva = new Reserva();
        nuevaReserva.setLibro(libro);
        nuevaReserva.setUsuario(usuario);

        // Guardar la reserva utilizando el servicio
        reservaService.reservarLibro(nuevaReserva);
        log.info("El usuario con correo {} ha realizado una reserva para el libro con ID {}", correoUsuario, id);

        // Agregar la reserva al modelo
        model.addAttribute("reserva", nuevaReserva);
        model.addAttribute("success", "Reserva realizada correctamente");

        // Redirige a la página de confirmación
        return "lector/reserva_confirmada";
    }

    @GetMapping("/lector/reserva_confirmada")
    public String mostrarReservaConfirmada(@RequestParam Long id, Model model) {
        Reserva reserva = reservaService.obtenerReservaPorId(id); // Obtener reserva desde el servicio

        if (reserva != null) {
            // Pasar la reserva directamente al modelo
            model.addAttribute("reserva", reserva);
            log.info("Se muestra la confirmación de reserva para la reserva con ID {}", id);
        } else {
            model.addAttribute("error", "La reserva no existe o no se encontró.");
            log.error("No se encontró la reserva con ID {}", id);
        }

        return "lector/reserva_confirmada";
    }

    @GetMapping("/historial")
    public String verHistorial(Model model) {
        // Obtener el usuario logueado
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String correoUsuario = user.getUsername();
        Usuario usuario = usuarioRepository.findByCorreo(correoUsuario)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        // Obtener las reservas y préstamos del usuario
        List<Reserva> reservas = reservaService.listarReservasPorUsuario(usuario.getId());
        List<Prestamo> prestamos = prestamoService.listarPrestamosPorUsuario(usuario.getId());

        // Añadir las listas al modelo
        model.addAttribute("reservas", reservas);
        model.addAttribute("prestamos", prestamos);

        log.info("Se muestran el historial de reservas y préstamos para el usuario con correo {}", correoUsuario);

        return "lector/historial"; // Nombre del archivo de la vista
    }

    @ModelAttribute
    public void agregarUsuarioLogueado(Model model) {
        try {
            // Obtener el correo del usuario logueado desde el contexto de seguridad
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String correoUsuario = user.getUsername();

            // Buscar al usuario por su correo
            Usuario usuario = usuarioRepository.findByCorreo(correoUsuario)
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

            // Agregar el nombre del usuario al modelo para que esté disponible en la vista
            model.addAttribute("nombreUsuario", usuario.getNombre());
            log.info("Se ha agregado al modelo el nombre del usuario con correo {}", correoUsuario);
        } catch (Exception e) {
            // Si no hay usuario logueado, no pasa nada
            model.addAttribute("nombreUsuario", null);
            log.warn("No hay usuario logueado, no se pudo agregar el nombre al modelo");
        }
    }
}
