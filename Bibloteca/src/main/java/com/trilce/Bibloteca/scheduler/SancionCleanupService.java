package com.trilce.Bibloteca.scheduler;

import java.time.LocalDate;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.trilce.Bibloteca.Entidades.Sancion;
import com.trilce.Bibloteca.repositorio.SancionRepository;

@Service

public class SancionCleanupService {

    private final SancionRepository sancionRepository;

    public SancionCleanupService(SancionRepository sancionRepository) {
        this.sancionRepository = sancionRepository;
    }

    @Scheduled(cron = "0 0 0 * * ?") // Ejecuta todos los días a medianoche
    public void limpiarSancionesVencidas() {
        List<Sancion> sanciones = sancionRepository.findAll();

        for (Sancion sancion : sanciones) {
            if (LocalDate.now().isAfter(sancion.getFechaFin())) {
                sancionRepository.delete(sancion); // Eliminar sanción vencida
            }
        }
    }
}