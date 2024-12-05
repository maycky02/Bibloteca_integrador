package com.trilce.Bibloteca.controladores;

import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.trilce.Bibloteca.Entidades.Sancion;
import com.trilce.Bibloteca.Entidades.Usuario;
import com.trilce.Bibloteca.servicios.SancionService;
import com.trilce.Bibloteca.servicios.UsuarioService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class SancionController {

    private final UsuarioService usuarioService;
    private final SancionService sancionService;

    // Mostrar la página de sanciones y buscar usuario por DNI
    @GetMapping("/admin/sanciones")
    public String mostrarSancionesPage(@RequestParam(required = false) String dni, Model model) {
        if (dni != null && !dni.isEmpty()) {
            try {
                Usuario usuario = usuarioService.buscarPorDni(dni);
                model.addAttribute("usuario", usuario);
            } catch (IllegalArgumentException e) {
                model.addAttribute("error", e.getMessage());
            }
        }
        return "admin/sanciones"; // Retorna la vista desde templates/admin/
    }

    // Registrar la sanción
    @PostMapping("/admin/sancionar-usuario")
    public String sancionarUsuario(@RequestParam Long usuarioId,
                                   @RequestParam String motivo,
                                   @RequestParam int diasSancion,
                                   Model model) {
        try {
            sancionService.registrarSancion(usuarioId, motivo, diasSancion);
            model.addAttribute("mensaje", "Sanción registrada correctamente.");
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
        }
        return "admin/sanciones"; // Siempre retorna a la página de sanciones
    }
    
 // Controlador para la gestión del reporte de sanciones y la eliminación de sanciones

 // Endpoint para mostrar el reporte de sanciones
 @GetMapping("/admin/reportedesanciones")
 public String mostrarReporteSanciones(Model model) {
     // Obtiene la lista de sanciones desde el servicio
     List<Sancion> sanciones = sancionService.listarSanciones();
     
     // Formateador de fechas para mostrar en el formato "dd/MM/yyyy"
     DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

     // Itera sobre cada sanción para formatear las fechas de inicio y fin
     sanciones.forEach(sancion -> {
         // Si la fecha de inicio no es nula, se formatea; en caso contrario, se establece "N/A"
         sancion.setFechaInicioFormatted(
             sancion.getFechaInicio() != null ? sancion.getFechaInicio().format(formatter) : "N/A"
         );
         // Si la fecha de fin no es nula, se formatea; en caso contrario, se establece "N/A"
         sancion.setFechaFinFormatted(
             sancion.getFechaFin() != null ? sancion.getFechaFin().format(formatter) : "N/A"
         );
     });

     // Agrega la lista de sanciones formateadas al modelo
     model.addAttribute("sanciones", sanciones);
     // Devuelve la vista correspondiente al reporte de sanciones
     return "admin/reportedesanciones";
 }

 // Endpoint para eliminar una sanción por su ID
 @GetMapping("/admin/eliminar-sancion/{id}")
 public String eliminarSancion(@PathVariable Long id, Model model) {
     try {
         // Intenta eliminar la sanción utilizando el servicio
         sancionService.eliminarSancion(id);
         // Agrega un mensaje de éxito al modelo
         model.addAttribute("mensaje", "Sanción eliminada correctamente.");
         // Log en la consola indicando que la sanción fue eliminada
         System.out.println("Sanción con ID " + id + " eliminada correctamente.");
     } catch (Exception e) {
         // En caso de error, agrega un mensaje de error al modelo
         model.addAttribute("error", "Ocurrió un error al intentar eliminar la sanción.");
         // Log en la consola con el mensaje de error
         System.out.println("Error al eliminar la sanción con ID " + id + ": " + e.getMessage());
     }
     // Redirige a la vista del reporte de sanciones
     return "redirect:/admin/reportedesanciones";
 }
  
}
