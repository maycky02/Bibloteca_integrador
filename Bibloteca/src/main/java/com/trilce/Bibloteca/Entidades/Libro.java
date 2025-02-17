package com.trilce.Bibloteca.Entidades;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.Data;

@Entity
@Data
public class Libro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;

    private String autor;

    private String editorial; 

    private String isbn;
    
    private LocalDate fechaPublicacion;
    
    private int stock;

    @Lob // Indicamos que este campo se debe tratar como un texto largo (TEXT)
    private String sinopsis;

    private String imagen;

}
