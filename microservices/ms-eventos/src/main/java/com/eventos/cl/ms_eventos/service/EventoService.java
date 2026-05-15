package com.eventos.cl.ms_eventos.service;

import com.eventos.cl.ms_eventos.client.TicketClient;
import com.eventos.cl.ms_eventos.dto.EventoRequestDTO;
import com.eventos.cl.ms_eventos.dto.EventoResponseDTO;
import com.eventos.cl.ms_eventos.dto.TicketDTO;
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
    private final TicketClient  ticketClient;

    // Metodo interno para mapear la Entidad al DTO de Salida
    private EventoResponseDTO mapearAResponse(Evento evento) {
        return new EventoResponseDTO(
                evento.getId(),
                evento.getNombre(),
                evento.getInformacionGeneral(),
                evento.getFechaHora(),
                evento.getUbicacion(),
                evento.getCapacidadMaxima(),
                evento.getEstado(),
                null
        );
    }

    public List<EventoResponseDTO> obtenerTodos() {
        return eventoRepository.findAll()
                .stream()
                .map(this::mapearAResponse)
                .collect(Collectors.toList());
    }

    public Optional<EventoResponseDTO> obtenerPorId(Long id) {
        Optional<Evento> resultado = eventoRepository.findById(id);

        if (!resultado.isPresent()) {
            throw new RuntimeException("El evento con la ID " + id + " no existe");
        }

        EventoResponseDTO response = mapearAResponse(resultado.get());

        try {

            Long idCategoria = 1L;

            System.out.println("Solicitando tickets filtrados para la categoría: " + idCategoria);
            List<TicketDTO> ticketsFiltrados = ticketClient.buscarPorCategoria(idCategoria);
            response.setTickets(ticketsFiltrados);
            System.out.println("¡Tickets filtrados acoplados con éxito!");

        } catch (Exception e) {

        System.out.println("--- ERROR DE CONEXIÓN CON MS-TICKETS ---");
        e.printStackTrace();
    }

        return Optional.of(response);
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
