package com.trilce.Bibloteca.Entidades;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Libro libro;

    @ManyToOne
    private Usuario usuario;  // Relación ManyToOne con Usuario

    private LocalDate fechaReserva;
    private String estado;  // Pendiente, Aprobada, Rechazada
    
    
}
