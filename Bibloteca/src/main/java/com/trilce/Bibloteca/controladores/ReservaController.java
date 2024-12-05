package com.trilce.Bibloteca.controladores;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.trilce.Bibloteca.Entidades.Reserva;
import com.trilce.Bibloteca.servicios.ReservaService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/admin/reservas")
@RequiredArgsConstructor
@Slf4j
public class ReservaController {

    private final ReservaService reservaService;

    // Listar las reservas y filtrar por DNI si es necesario
    @GetMapping
    public String listarReservas(@RequestParam(required = false) String dni, Model model) {
        List<Reserva> reservas = reservaService.listarReservas();
        log.info("Listando reservas, total de reservas: {}", reservas.size());

        if (dni != null && !dni.isEmpty()) {
            reservas = reservas.stream()
                .filter(reserva -> reserva.getUsuario() != null && reserva.getUsuario().getDni().equals(dni))
                .collect(Collectors.toList());
            model.addAttribute("dni", dni);  // Agregar el DNI al modelo para prellenar el filtro
            log.info("Filtrado de reservas por DNI: {}", dni);
        }

        model.addAttribute("reservas", reservas);
        return "admin/reservas"; // Nombre de la vista
    }

    // Aprobar una reserva y crear un préstamo
    @PostMapping("/{id}/aprobar")
    public String aprobarReserva(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        reservaService.aprobarReserva(id);
        reservaService.crearPrestamoDesdeReserva(id);
        
        log.info("Reserva aprobada con ID: {}. Préstamo creado.", id);
        redirectAttributes.addFlashAttribute("success", "Reserva aprobada y préstamo creado.");
        return "redirect:/admin/prestamos"; // Redirigir a la lista de préstamos
    }

    // Rechazar una reserva y actualizar el stock
    @PostMapping("/{id}/rechazar")
    public String rechazarReserva(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        reservaService.rechazarReserva(id);
        
        log.info("Reserva rechazada con ID: {}. Stock actualizado.", id);
        redirectAttributes.addFlashAttribute("success", "Reserva rechazada y stock actualizado.");
        return "redirect:/admin/reservas"; // Redirigir a la vista de reservas
    }

    // Realizar una reserva de un libro
    @PostMapping("/reservar")
    public String realizarReserva(Reserva reserva, RedirectAttributes redirectAttributes) {
        String mensaje = reservaService.reservarLibro(reserva);  // Llamar al servicio para realizar la reserva

        if (mensaje.startsWith("No puedes realizar una reserva")) {
            log.warn("Reserva rechazada. Usuario sancionado. Mensaje: {}", mensaje);
            redirectAttributes.addFlashAttribute("sancionMensaje", mensaje);  // Mensaje de sanción
            return "redirect:/lector/catalogo";  // Redirigir al catálogo si está sancionado
        } else {
            log.info("Reserva realizada exitosamente. Mensaje: {}", mensaje);
            redirectAttributes.addFlashAttribute("success", mensaje);  // Mensaje de éxito
            return "redirect:/lector/reserva_confirmada";  // Redirigir a la página de confirmación
        }
    }
}
