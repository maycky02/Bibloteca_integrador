package com.trilce.Bibloteca.servicios;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.trilce.Bibloteca.Entidades.Prestamo;
import com.trilce.Bibloteca.Entidades.Reserva;

import com.trilce.Bibloteca.repositorio.LibroRepository;
import com.trilce.Bibloteca.repositorio.PrestamoRepository;
import com.trilce.Bibloteca.repositorio.ReservaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PrestamoService {

    private final PrestamoRepository prestamoRepository;
    private final LibroRepository libroRepository;  // Repositorio de libros
    
    private final ReservaRepository reservaRepository;    // Servicio de sanciones

    /**
     * Método para devolver un préstamo.
     * @param prestamoId ID del préstamo a devolver.
     * @return boolean indicando si el préstamo fue devuelto tarde.
     */
    public boolean devolverPrestamo(Long prestamoId) {
        // Buscar el préstamo
        Prestamo prestamo = prestamoRepository.findById(prestamoId)
            .orElseThrow(() -> new IllegalArgumentException("Préstamo no encontrado"));

        // Verificar estado actual
        if (!"Activo".equals(prestamo.getEstado())) {
            throw new IllegalStateException("El préstamo ya ha sido devuelto o no está activo.");
        }

        // Verificar si fue devuelto tarde
        boolean devueltoTarde = LocalDate.now().isAfter(prestamo.getFechaDevolucion());
        prestamo.setDevueltoTarde(devueltoTarde);

        // Cambiar estado a "Devuelto"
        prestamo.setEstado("Devuelto");
        prestamoRepository.save(prestamo);

        // Actualizar el stock del libro
        prestamo.getLibro().setStock(prestamo.getLibro().getStock() + 1);
        libroRepository.save(prestamo.getLibro());

        return devueltoTarde; // Retorna si la devolución fue tarde
    }

    /**
     * Método para listar todos los préstamos.
     * @return Lista de todos los préstamos registrados.
     */
    public List<Prestamo> listarTodos() {
        return prestamoRepository.findAll();  // Devuelve todos los préstamos registrados
    }

    /**
     * Método para listar los préstamos vencidos.
     * @return Lista de préstamos cuyo estado no es "Devuelto" y cuya fecha de devolución ha pasado.
     */
    public List<Prestamo> listarPrestamosVencidos() {
        return prestamoRepository.findAll().stream()
            .filter(prestamo -> prestamo.getFechaDevolucion().isBefore(LocalDate.now()) && !"Devuelto".equals(prestamo.getEstado()))
            .collect(Collectors.toList());
    }

    /**
     * Método para actualizar un préstamo existente.
     * @param prestamo Objeto préstamo con los nuevos datos.
     * @return El préstamo actualizado.
     */
    public Prestamo actualizarPrestamo(Prestamo prestamo) {
        return prestamoRepository.save(prestamo);
    }

    /**
     * Método para buscar un préstamo por ID.
     * @param id ID del préstamo a buscar.
     * @return Un Optional que contiene el préstamo si se encuentra.
     */
    public Optional<Prestamo> buscarPrestamoPorId(Long id) {
        return prestamoRepository.findById(id);
    }

    /**
     * Método para listar todos los préstamos.
     * @return Lista de todos los préstamos registrados.
     */
    public List<Prestamo> listarPrestamos() {
        return prestamoRepository.findAll();  
    }

    /**
     * Método para crear un préstamo basado en una reserva aprobada.
     * @param reservaId ID de la reserva aprobada.
     */
    public void crearPrestamoDesdeReserva(Long reservaId) {
        // Obtener la reserva aprobada
        Reserva reserva = reservaRepository.findById(reservaId)
            .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada"));

        // Crear un nuevo préstamo basado en la reserva
        Prestamo prestamo = new Prestamo();
        prestamo.setLibro(reserva.getLibro());
        prestamo.setUsuario(reserva.getUsuario());
        prestamo.setFechaPrestamo(LocalDate.now());
        prestamo.setFechaDevolucion(LocalDate.now().plusWeeks(1));  // Prestamo por una semana
        prestamo.setEstado("Activo");

        // Guardar el préstamo en la base de datos
        prestamoRepository.save(prestamo);
    }

    /**
     * Método para obtener todos los préstamos registrados.
     * @return Lista de todos los préstamos.
     */
    public List<Prestamo> obtenerTodosLosPrestamos() {
        return prestamoRepository.findAll();
    }

    /**
     * Método para obtener préstamos por el DNI del usuario.
     * @param dni DNI del usuario.
     * @return Lista de préstamos asociados al usuario.
     */
    public List<Prestamo> obtenerPrestamosPorDni(String dni) {
        return prestamoRepository.findByUsuarioDni(dni); // Método de consulta en el repositorio
    }

    /**
     * Método para listar préstamos por usuario.
     * @param usuarioId ID del usuario.
     * @return Lista de préstamos asociados al usuario.
     */
    public List<Prestamo> listarPrestamosPorUsuario(Long usuarioId) {
        return prestamoRepository.findByUsuarioId(usuarioId);
    }
}
