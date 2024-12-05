package com.trilce.Bibloteca.servicios;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.trilce.Bibloteca.Entidades.Libro;
import com.trilce.Bibloteca.Entidades.Prestamo;
import com.trilce.Bibloteca.Entidades.Reserva;
import com.trilce.Bibloteca.Entidades.Usuario;
import com.trilce.Bibloteca.repositorio.LibroRepository;
import com.trilce.Bibloteca.repositorio.PrestamoRepository;
import com.trilce.Bibloteca.repositorio.ReservaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final LibroRepository libroRepository;
    private final PrestamoRepository prestamoRepository;

    /**
     * Realiza una reserva de un libro.
     * @param reserva Datos de la reserva.
     * @return Mensaje indicando el resultado de la operación.
     */
    public String reservarLibro(Reserva reserva) {
        Libro libro = reserva.getLibro();
        Usuario usuario = reserva.getUsuario();

        // Verificar si el usuario tiene una sanción activa.
        if (usuario.getFechaSancion() != null && usuario.getFechaSancion().isAfter(LocalDate.now())) {
            return "No puedes realizar una reserva porque tienes una sanción activa hasta el " + usuario.getFechaSancion();
        }

        // Verificar si hay stock disponible.
        if (libro.getStock() > 0) {
            libro.setStock(libro.getStock() - 1);
            libroRepository.save(libro);

            reserva.setFechaReserva(LocalDate.now());
            reserva.setEstado("Pendiente");
            reservaRepository.save(reserva);
            return "Reserva realizada con éxito.";
        } else {
            return "No hay stock disponible para este libro.";
        }
    }

    /**
     * Rechaza una reserva específica.
     * @param id ID de la reserva.
     */
    public void rechazarReserva(Long id) {
        Reserva reserva = reservaRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada"));

        // Devolver el libro a su stock original.
        Libro libro = reserva.getLibro();
        libro.setStock(libro.getStock() + 1);
        libroRepository.save(libro);

        // Cambiar el estado de la reserva a "Rechazada".
        reserva.setEstado("Rechazada");
        reservaRepository.save(reserva);
    }

    /**
     * Aprueba una reserva específica.
     * @param reservaId ID de la reserva a aprobar.
     */
    public void aprobarReserva(Long reservaId) {
        Reserva reserva = reservaRepository.findById(reservaId)
            .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada"));

        reserva.setEstado("Aprobada");
        reservaRepository.save(reserva);
    }

    /**
     * Cancela todas las reservas vencidas.
     */
    public void cancelarReservasVencidas() {
        List<Reserva> todasLasReservas = reservaRepository.findAll();

        // Identificar reservas vencidas con estado "Pendiente".
        List<Reserva> reservasVencidas = todasLasReservas.stream()
            .filter(reserva -> reserva.getFechaReserva().isBefore(LocalDate.now()) && "Pendiente".equals(reserva.getEstado()))
            .collect(Collectors.toList());

        // Cambiar el estado de las reservas vencidas a "Cancelada".
        for (Reserva reserva : reservasVencidas) {
            reserva.setEstado("Cancelada");
            reservaRepository.save(reserva);
        }
    }

    /**
     * Lista todas las reservas.
     * @return Lista de reservas.
     */
    public List<Reserva> listarReservas() {
        return reservaRepository.findAll();
    }

    /**
     * Crea un préstamo basado en una reserva aprobada.
     * @param reservaId ID de la reserva aprobada.
     */
    public void crearPrestamoDesdeReserva(Long reservaId) {
        Reserva reserva = reservaRepository.findById(reservaId)
            .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada"));

        Prestamo prestamo = new Prestamo();
        prestamo.setLibro(reserva.getLibro());
        prestamo.setUsuario(reserva.getUsuario());
        prestamo.setFechaPrestamo(LocalDate.now());
        prestamo.setFechaDevolucion(LocalDate.now().plusWeeks(1));  // Préstamo por una semana.
        prestamo.setEstado("Activo");

        prestamoRepository.save(prestamo);
    }

    /**
     * Obtiene los detalles de una reserva por su ID.
     * @param id ID de la reserva.
     * @return Detalles de la reserva.
     */
    public Reserva obtenerReservaPorId(Long id) {
        return reservaRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada"));
    }

    /**
     * Lista las reservas asociadas a un usuario específico.
     * @param usuarioId ID del usuario.
     * @return Lista de reservas del usuario.
     */
    public List<Reserva> listarReservasPorUsuario(Long usuarioId) {
        return reservaRepository.findByUsuarioId(usuarioId);
    }
}
