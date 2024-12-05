package com.trilce.Bibloteca.repositorio;

import com.trilce.Bibloteca.Entidades.Libro;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LibroRepository extends JpaRepository<Libro, Long> {
}
