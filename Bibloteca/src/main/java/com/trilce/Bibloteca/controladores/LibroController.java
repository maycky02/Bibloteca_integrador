package com.trilce.Bibloteca.controladores;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.trilce.Bibloteca.Entidades.Libro;
import com.trilce.Bibloteca.repositorio.LibroRepository;
import com.trilce.Bibloteca.servicios.LibroService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/admin/libros")
@RequiredArgsConstructor
@Slf4j
public class LibroController {

    private final LibroService libroService;
    private final LibroRepository libroRepository;

    // Mostrar la lista de libros
    @GetMapping
    public String listarLibros(Model model) {
        List<Libro> libros = libroService.listarLibros();
        model.addAttribute("libros", libros);
        log.info("Listado de libros obtenido exitosamente.");
        return "admin/libros"; // Vista de la lista de libros
    }

    // Mostrar detalles del libro
    @GetMapping("/{id}")
    public String verLibro(@PathVariable Long id, Model model) {
        Libro libro = libroService.obtenerLibro(id);
        model.addAttribute("libro", libro);
        log.info("Mostrando detalles del libro con ID: {}", id);
        return "admin/verLibro"; // Vista para mostrar detalles del libro
    }

    // Mostrar el formulario para editar el libro
    @GetMapping("/{id}/editar")
    public String editarLibro(@PathVariable Long id, Model model) {
        Libro libro = libroService.obtenerLibro(id);
        model.addAttribute("libro", libro);
        log.info("Formulario de edición de libro con ID: {}", id);
        return "admin/libroFormulario"; // Vista para editar libro
    }

    // Guardar o actualizar el libro
    @PostMapping("/guardar")
    public String guardarLibro(@Valid @ModelAttribute Libro libro,
                                BindingResult result,
                                Model model,
                                @RequestParam("imagenArchivo") MultipartFile imagen,
                                RedirectAttributes attribute) {

        if (result.hasErrors()) {
            model.addAttribute("titulo", "Formulario Nuevo Libro");
            model.addAttribute("libro", libro);
            attribute.addFlashAttribute("warning", "Existen errores en el formulario");
            log.warn("Errores en el formulario al guardar el libro: {}", libro);
            return "views/admin/formLibro";
        }

        if (!imagen.isEmpty()) {
            // Ruta donde guardarás las imágenes
            Path directorioImagenes = Paths.get("src/main/resources/static/img_libros");
            File directorio = directorioImagenes.toAbsolutePath().toFile();

            if (!directorio.exists()) {
                // Crear el directorio si no existe
                directorio.mkdirs();
                log.info("Directorio para imágenes creado: {}", directorio.getAbsolutePath());
            }

            try {
                // Leer los bytes de la imagen
                byte[] bytesImg = imagen.getBytes();

                // Crear el path completo para guardar la imagen
                Path rutaCompleta = Paths.get(directorio.getAbsolutePath(), imagen.getOriginalFilename());

                // Escribir los bytes de la imagen
                Files.write(rutaCompleta, bytesImg);
                log.info("Imagen guardada en la ruta: {}", rutaCompleta.toString());

                // Asociar el nombre de la imagen al libro
                libro.setImagen(imagen.getOriginalFilename());
            } catch (IOException e) {
                log.error("Error al guardar la imagen para el libro: {}", libro.getTitulo(), e);
                attribute.addFlashAttribute("error", "Hubo un problema al guardar la imagen");
                return "redirect:/admin/libros/nuevo"; // Redirige con mensaje de error
            }
        }

        // Guardar el libro en la base de datos
        libroRepository.save(libro);
        log.info("Libro guardado exitosamente con ID: {}", libro.getId());
        attribute.addFlashAttribute("success", "Libro guardado exitosamente");
        return "redirect:/admin/libros";
    }

    // Eliminar un libro
    @PostMapping("/{id}/eliminar")
    public String eliminarLibro(@PathVariable Long id) {
        libroService.eliminarLibro(id);
        log.info("Libro con ID: {} eliminado exitosamente", id);
        return "redirect:/admin/libros"; // Redirige a la lista de libros después de eliminar
    }

    // Mostrar formulario para nuevo libro
    @GetMapping("/nuevo")
    public String nuevoLibro(Model model) {
        model.addAttribute("libro", new Libro()); // Nuevo libro vacío
        log.info("Mostrando formulario para nuevo libro.");
        return "admin/libroFormulario"; // Vista del formulario de nuevo libro
    }
}
