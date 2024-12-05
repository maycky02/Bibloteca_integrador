package com.trilce.Bibloteca.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.trilce.Bibloteca.Entidades.Reserva;
import com.trilce.Bibloteca.Entidades.Usuario;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    
    List<Reserva> findAll();  // Esta consulta ya incluye las relaciones con los usuarios gracias a JPA
    List<Reserva> findByUsuario(Usuario usuario);
	List<Reserva> findByUsuarioId(Long usuarioId);
}
