package com.trilce.Bibloteca.Entidades;

import java.time.LocalDate;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Getter
@Setter
@NoArgsConstructor  // Constructor sin parámetros para Hibernate
@AllArgsConstructor // Constructor con todos los parámetros (útil para crear instancias rápidamente)
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;  

    private String apellido;  

    private String correo;

    private String dni;

    private String contraseña;

    

    @Enumerated(EnumType.STRING)
    private Rol rol;

    public enum Rol {
        ADMINISTRADOR, LECTOR
    }

    private LocalDate fechaSancion; 
}
