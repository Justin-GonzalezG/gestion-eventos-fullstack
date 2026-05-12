package com.eventos.cl.ms_eventos.service;

import com.eventos.cl.ms_eventos.dto.EventoRequestDTO;
import com.eventos.cl.ms_eventos.dto.EventoResponseDTO;
import com.eventos.cl.ms_eventos.model.Evento;
import com.eventos.cl.ms_eventos.repository.EventoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class EventoService {

    private final EventoRepository eventoRepository;

    // Metodo interno para mapear la Entidad al DTO de Salida
    private EventoResponseDTO mapearAResponse(Evento evento) {
        return new EventoResponseDTO(
                evento.getId(),
                evento.getNombre(),
                evento.getInformacionGeneral(),
                evento.getFechaHora(),
                evento.getUbicacion(),
                evento.getCapacidadMaxima(),
                evento.getEstado()
        );
    }

    public List<EventoResponseDTO> obtenerTodos() {
        return eventoRepository.findAll()
                .stream()
                .map(this::mapearAResponse)
                .collect(Collectors.toList());
    }

    public Optional<EventoResponseDTO> obtenerPorId(Long id) {
        return eventoRepository.findById(id).map(this::mapearAResponse);
    }

    public EventoResponseDTO guardar(EventoRequestDTO dto) {
        // Transformar el RequestDTO a la Entidad base
        Evento evento = new Evento();
        evento.setNombre(dto.getNombre());
        evento.setInformacionGeneral(dto.getInformacionGeneral());
        evento.setFechaHora(dto.getFechaHora());
        evento.setUbicacion(dto.getUbicacion());
        evento.setCapacidadMaxima(dto.getCapacidadMaxima());
        evento.setEstado(dto.getEstado());

        Evento guardado = eventoRepository.save(evento);
        return mapearAResponse(guardado);
    }

    public Optional<EventoResponseDTO> actualizar(Long id, EventoRequestDTO dto) {
        return eventoRepository.findById(id).map(evento -> {
            evento.setNombre(dto.getNombre());
            evento.setInformacionGeneral(dto.getInformacionGeneral());
            evento.setFechaHora(dto.getFechaHora());
            evento.setUbicacion(dto.getUbicacion());
            evento.setCapacidadMaxima(dto.getCapacidadMaxima());
            evento.setEstado(dto.getEstado());
            
            Evento actualizado = eventoRepository.save(evento);
            return mapearAResponse(actualizado);
        });
    }

    public void eliminar(Long id) {
        eventoRepository.deleteById(id);
    }
}
