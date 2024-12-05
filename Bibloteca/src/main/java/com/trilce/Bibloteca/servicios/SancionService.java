package com.trilce.Bibloteca.servicios;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.trilce.Bibloteca.Entidades.Sancion;
import com.trilce.Bibloteca.Entidades.Usuario;
import com.trilce.Bibloteca.repositorio.SancionRepository;
import com.trilce.Bibloteca.repositorio.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SancionService {

    private final SancionRepository sancionRepository;
    private final UsuarioRepository usuarioRepository;

    /**
     * Registra una nueva sanción para un usuario.
     *
     * @param usuarioId   ID del usuario al que se le aplicará la sanción.
     * @param motivo      Razón por la cual se aplica la sanción.
     * @param diasSancion Número de días que durará la sanción.
     */
    public void registrarSancion(Long usuarioId, String motivo, int diasSancion) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + usuarioId));

        Sancion sancion = new Sancion();
        sancion.setUsuario(usuario);
        sancion.setMotivo(motivo);
        sancion.setFechaInicio(LocalDate.now());
        sancion.setFechaFin(LocalDate.now().plusDays(diasSancion));

        sancionRepository.save(sancion);
    }

    /**
     * Lista todas las sanciones existentes y formatea las fechas para mostrar en formato "dd/MM/yyyy".
     *
     * @return Lista de todas las sanciones con fechas formateadas.
     */
    public List<Sancion> listarSanciones() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return sancionRepository.findAll().stream().peek(sancion -> {
            if (sancion.getFechaInicio() != null) {
                sancion.setFechaInicioFormatted(sancion.getFechaInicio().format(formatter));
            }
            if (sancion.getFechaFin() != null) {
                sancion.setFechaFinFormatted(sancion.getFechaFin().format(formatter));
            }
        }).collect(Collectors.toList());
    }

    /**
     * Elimina una sanción por su ID.
     *
     * @param id ID de la sanción a eliminar.
     */
    public void eliminarSancion(Long id) {
        sancionRepository.deleteById(id);
    }

    /**
     * Verifica si un usuario tiene una sanción activa.
     *
     * @param usuarioId ID del usuario.
     * @return true si el usuario tiene una sanción activa, false en caso contrario.
     */
    public boolean tieneSancionActiva(Long usuarioId) {
        return sancionRepository.findByUsuarioIdAndFechaFinAfter(usuarioId, LocalDate.now()).size() > 0;
    }

    /**
     * Busca una sanción específica por su ID.
     *
     * @param sancionId ID de la sanción.
     * @return La sanción encontrada o null si no existe.
     */
    public Sancion buscarSancionPorId(Long sancionId) {
        return sancionRepository.findById(sancionId)
                .orElse(null);
    }

    /**
     * Lista todas las sanciones activas (aquellas cuya fecha de fin es posterior a la fecha actual).
     *
     * @return Lista de sanciones activas.
     */
    public List<Sancion> listarSancionesActivas() {
        return sancionRepository.findAll().stream()
                .filter(sancion -> LocalDate.now().isBefore(sancion.getFechaFin()))
                .collect(Collectors.toList());
    }

    /**
     * Registra una nueva sanción.
     *
     * @param sancion La sanción a registrar.
     */
    public void registrarSancion(Sancion sancion) {
        sancionRepository.save(sancion);
    }

    /**
     * Obtiene la sanción activa de un usuario.
     *
     * @param usuarioId ID del usuario.
     * @return La sanción activa del usuario o null si no tiene sanciones activas.
     */
    public Sancion obtenerSancionActiva(Long usuarioId) {
        return sancionRepository.findByUsuarioIdAndFechaFinAfter(usuarioId, LocalDate.now())
                .stream()
                .findFirst()
                .orElse(null);
    }
}
