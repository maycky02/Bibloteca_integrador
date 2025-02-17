package com.trilce.Bibloteca.repositorio;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.trilce.Bibloteca.Entidades.Prestamo;

@Repository
public interface PrestamoRepository extends JpaRepository<Prestamo, Long> {

    List<Prestamo> findByUsuarioDni(String dni);

    
    List<Prestamo> findByFechaDevolucionBeforeAndEstadoNot(LocalDate fechaDevolucion, String estado);

	List<Prestamo> findByUsuarioId(Long usuarioId);

}
