package com.eventos.cl.ms_recomendacion.service;

import com.eventos.cl.ms_recomendacion.client.EventoClient;
import com.eventos.cl.ms_recomendacion.client.UsuarioClient;
import com.eventos.cl.ms_recomendacion.dto.EventoDTO;
import com.eventos.cl.ms_recomendacion.dto.UsuarioDTO;
import com.eventos.cl.ms_recomendacion.dto.RecomendacionConDetalleDTO;
import com.eventos.cl.ms_recomendacion.dto.RecomendacionRequestDTO;
import com.eventos.cl.ms_recomendacion.dto.RecomendacionResponseDTO;
import com.eventos.cl.ms_recomendacion.model.Recomendacion;
import com.eventos.cl.ms_recomendacion.repository.RecomendacionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class RecomendacionService {

    private final RecomendacionRepository recomendacionRepository;
    
    // Inyectamos nuestras antenas Feign para poder hacer llamadas a otros microservicios
    private final EventoClient eventoClient;
    private final UsuarioClient usuarioClient;

    private RecomendacionResponseDTO mapearAResponse(Recomendacion recomendacion) {
        return new RecomendacionResponseDTO(
                recomendacion.getId(),
                recomendacion.getUsuarioId(),
                recomendacion.getEventoSugeridoId(),
                recomendacion.getMotivo(),
                recomendacion.getNivelAfinidad(),
                recomendacion.getFechaRecomendacion()
        );
    }

    public List<RecomendacionResponseDTO> obtenerTodas() {
        return recomendacionRepository.findAll()
                .stream()
                .map(this::mapearAResponse)
                .collect(Collectors.toList());
    }

    public Optional<RecomendacionResponseDTO> obtenerPorId(Long id) {
        return recomendacionRepository.findById(id).map(this::mapearAResponse);
    }

    public RecomendacionResponseDTO guardar(RecomendacionRequestDTO dto) {
        Recomendacion recomendacion = new Recomendacion();
        recomendacion.setUsuarioId(dto.getUsuarioId());
        recomendacion.setEventoSugeridoId(dto.getEventoSugeridoId());
        recomendacion.setMotivo(dto.getMotivo());
        recomendacion.setNivelAfinidad(dto.getNivelAfinidad());
        // El servidor inyecta la fecha automatica
        recomendacion.setFechaRecomendacion(new Date());

        Recomendacion guardado = recomendacionRepository.save(recomendacion);
        return mapearAResponse(guardado);
    }

    public Optional<RecomendacionResponseDTO> actualizar(Long id, RecomendacionRequestDTO dto) {
        return recomendacionRepository.findById(id).map(recomendacion -> {
            recomendacion.setUsuarioId(dto.getUsuarioId());
            recomendacion.setEventoSugeridoId(dto.getEventoSugeridoId());
            recomendacion.setMotivo(dto.getMotivo());
            recomendacion.setNivelAfinidad(dto.getNivelAfinidad());
            
            Recomendacion actualizado = recomendacionRepository.save(recomendacion);
            return mapearAResponse(actualizado);
        });
    }

    public void eliminar(Long id) {
        recomendacionRepository.deleteById(id);
    }

    // Busca la recomendación propia y le anexa los datos de múltiples servicios
    public Optional<RecomendacionConDetalleDTO> obtenerDetalleCompleto(Long id) {
        return recomendacionRepository.findById(id).map(recomendacion -> {
            
            // 1. Llamada Feign al ms-eventos
            EventoDTO eventoDTO = null;
            try {
                eventoDTO = eventoClient.obtenerEventoPorId(recomendacion.getEventoSugeridoId()).getBody();
            } catch (Exception e) {
                System.out.println("No se pudo obtener el detalle del evento: " + e.getMessage());
            }

            // 2. Llamada Feign al ms-usuarios
            UsuarioDTO usuarioDTO = null;
            try {
                usuarioDTO = usuarioClient.obtenerUsuarioPorId(recomendacion.getUsuarioId()).getBody();
            } catch (Exception e) {
                System.out.println("No se pudo obtener el detalle del usuario: " + e.getMessage());
            }

            // Retornamos el súper-DTO fusionando las TRES informaciones
            return new RecomendacionConDetalleDTO(
                    recomendacion.getId(),
                    recomendacion.getUsuarioId(),
                    recomendacion.getMotivo(),
                    recomendacion.getNivelAfinidad(),
                    recomendacion.getFechaRecomendacion().toString(),
                    eventoDTO,
                    usuarioDTO
            );
        });
    }
}
