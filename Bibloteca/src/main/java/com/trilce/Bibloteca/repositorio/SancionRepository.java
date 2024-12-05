package com.trilce.Bibloteca.repositorio;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.trilce.Bibloteca.Entidades.Sancion;

@Repository
public interface SancionRepository extends JpaRepository<Sancion, Long> {
    List<Sancion> findByUsuarioId(Long usuarioId);
    List<Sancion> findByUsuarioIdAndFechaFinAfter(Long usuarioId, LocalDate fechaActual);

}
