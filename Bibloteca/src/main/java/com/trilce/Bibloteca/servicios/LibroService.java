package com.trilce.Bibloteca.servicios;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.trilce.Bibloteca.Entidades.Libro;
import com.trilce.Bibloteca.repositorio.LibroRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LibroService {

    private final LibroRepository libroRepository;

    /** 
     * Método para agregar un nuevo libro.
     * @param libro Datos del libro a agregar.
     * @return El libro agregado.
     */
    public Libro agregarLibro(Libro libro) {
        return libroRepository.save(libro);
    }

    /** 
     * Método para editar un libro existente.
     * @param id ID del libro a editar.
     * @param libroActualizado Datos actualizados del libro.
     * @return El libro actualizado.
     */
    public Libro editarLibro(Long id, Libro libroActualizado) {
        Libro libro = libroRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Libro no encontrado"));
        libro.setTitulo(libroActualizado.getTitulo());
        libro.setAutor(libroActualizado.getAutor());
        libro.setFechaPublicacion(libroActualizado.getFechaPublicacion());
        libro.setEditorial(libroActualizado.getEditorial());
        libro.setSinopsis(libroActualizado.getSinopsis());
        libro.setIsbn(libroActualizado.getIsbn());
        libro.setStock(libroActualizado.getStock());
        return libroRepository.save(libro);
    }

    /** 
     * Método para eliminar un libro por su ID.
     * @param id ID del libro a eliminar.
     */
    public void eliminarLibro(Long id) {
        if (!libroRepository.existsById(id)) {
            throw new IllegalArgumentException("Libro no encontrado");
        }
        libroRepository.deleteById(id);
    }

    /** 
     * Método para listar todos los libros.
     * @return Lista de todos los libros.
     */
    public List<Libro> listarLibros() {
        return libroRepository.findAll();
    }

    /** 
     * Método para buscar un libro por su ID.
     * @param id ID del libro a buscar.
     * @return Optional del libro encontrado.
     */
    public Optional<Libro> buscarLibroPorId(Long id) {
        return libroRepository.findById(id);
    }

    /** 
     * Método para actualizar un libro existente.
     * @param libro Datos del libro actualizado.
     */
    public void actualizarLibro(Libro libro) {
        libroRepository.save(libro);
    }

    /** 
     * Método para obtener un libro por su ID.
     * @param id ID del libro a obtener.
     * @return El libro encontrado.
     */
    public Libro obtenerLibro(Long id) {
        return libroRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Libro no encontrado con ID: " + id));
    }

    /** 
     * Método para obtener todos los libros registrados.
     * @return Lista de todos los libros.
     */
    public List<Libro> obtenerTodosLosLibros() {
        return libroRepository.findAll();
    }
}
