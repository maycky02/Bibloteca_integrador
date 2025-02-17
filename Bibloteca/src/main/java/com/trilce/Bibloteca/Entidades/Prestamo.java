package com.trilce.Bibloteca.Entidades;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
@Entity
@Data
public class Prestamo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Libro libro;

    @ManyToOne
    private Usuario usuario;

    private LocalDate fechaPrestamo;

    private LocalDate fechaDevolucion = LocalDate.now();;

    private String estado; // Activo, Devuelto

    // Método que verifica si el préstamo ha sido devuelto
    public boolean isDevuelto() {
        return "Devuelto".equals(this.estado);
    }
    private boolean devueltoTarde; // Indica si la devolución fue tarde

 

}
