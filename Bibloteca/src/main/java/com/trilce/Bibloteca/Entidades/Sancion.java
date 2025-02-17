package com.trilce.Bibloteca.Entidades;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Entity
@Data
public class Sancion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    private String motivo;

    private LocalDate fechaInicio;

    private LocalDate fechaFin;

    private boolean activo = true;

    @Transient
    @Getter
    @Setter
    private String fechaInicioFormatted;

    @Transient
    @Getter
    @Setter
    private String fechaFinFormatted;

    public long getDiasDeSancion() {
        return ChronoUnit.DAYS.between(fechaInicio, fechaFin);
    }
}
