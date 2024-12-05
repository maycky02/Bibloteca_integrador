package com.trilce.Bibloteca.controladores;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.trilce.Bibloteca.Entidades.Prestamo;
import com.trilce.Bibloteca.servicios.PrestamoService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/admin/prestamos")
@RequiredArgsConstructor
@Slf4j
public class PrestamoController {

    private final PrestamoService prestamoService;
 
    // Mostrar lista de préstamos
    @GetMapping
    public String listarPrestamos(@RequestParam(required = false) String dni, Model model) {
        List<Prestamo> prestamos;

        if (dni != null && !dni.isEmpty()) {
            // Si se proporciona un DNI, filtrar los préstamos por DNI
            prestamos = prestamoService.obtenerPrestamosPorDni(dni);
            log.info("Se han filtrado los préstamos por DNI: {}", dni);
        } else {
            // Si no se proporciona un DNI, obtener todos los préstamos
            prestamos = prestamoService.obtenerTodosLosPrestamos();
            log.info("Se han obtenido todos los préstamos");
        }

        model.addAttribute("prestamos", prestamos);
        model.addAttribute("dni", dni); // Se pasa el DNI al formulario para que se mantenga en el campo de texto
        return "admin/prestamos";
    }

    @PostMapping("/{id}/devolver")
    public String devolverPrestamo(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        log.info("Iniciando la devolución del préstamo con ID: {}", id);
        
        // Llamar al servicio para realizar la devolución y obtener si fue tarde
        boolean devueltoTarde = prestamoService.devolverPrestamo(id);
        
        // Si fue devuelto tarde, añadimos un mensaje de advertencia
        if (devueltoTarde) {
            redirectAttributes.addFlashAttribute("mensaje", "Devolución tardía. Se aplicará una sanción.");
            log.warn("La devolución del préstamo con ID: {} fue tardía", id);
        } else {
            redirectAttributes.addFlashAttribute("mensajeExito", "Libro devuelto con éxito.");
            log.info("La devolución del préstamo con ID: {} se realizó exitosamente", id);
        }

        return "redirect:/admin/prestamos"; // Redirigir a la lista de préstamos
    }
}
