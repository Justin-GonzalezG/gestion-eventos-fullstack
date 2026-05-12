package com.eventos.cl.ms_soporte.service;

import com.eventos.cl.ms_soporte.dto.TicketRequestDTO;
import com.eventos.cl.ms_soporte.dto.TicketResponseDTO;
import com.eventos.cl.ms_soporte.model.TicketSoporte;
import com.eventos.cl.ms_soporte.repository.TicketSoporteRepository;
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
public class TicketSoporteService {

    private final TicketSoporteRepository ticketSoporteRepository;

    private TicketResponseDTO mapearAResponse(TicketSoporte ticket) {
        return new TicketResponseDTO(
                ticket.getId(),
                ticket.getUsuarioId(),
                ticket.getOrdenId(),
                ticket.getAsunto(),
                ticket.getDescripcionProblema(),
                ticket.getEstado(),
                ticket.getFechaCreacion()
        );
    }

    public List<TicketResponseDTO> obtenerTodos() {
        return ticketSoporteRepository.findAll()
                .stream()
                .map(this::mapearAResponse)
                .collect(Collectors.toList());
    }

    public Optional<TicketResponseDTO> obtenerPorId(Long id) {
        return ticketSoporteRepository.findById(id).map(this::mapearAResponse);
    }

    public TicketResponseDTO guardar(TicketRequestDTO dto) {
        TicketSoporte ticket = new TicketSoporte();
        ticket.setUsuarioId(dto.getUsuarioId());
        ticket.setOrdenId(dto.getOrdenId());
        ticket.setAsunto(dto.getAsunto());
        ticket.setDescripcionProblema(dto.getDescripcionProblema());
        
        // Inyecciones automaticas del sistema
        ticket.setEstado("ABIERTO");
        ticket.setFechaCreacion(new Date());

        TicketSoporte guardado = ticketSoporteRepository.save(ticket);
        return mapearAResponse(guardado);
    }

    // Usaremos esto cuando un admin quiera cerrar un ticket o ponerlo en proceso
    public Optional<TicketResponseDTO> actualizarEstado(Long id, String nuevoEstado) {
        return ticketSoporteRepository.findById(id).map(ticket -> {
            ticket.setEstado(nuevoEstado);
            TicketSoporte actualizado = ticketSoporteRepository.save(ticket);
            return mapearAResponse(actualizado);
        });
    }

    public void eliminar(Long id) {
        ticketSoporteRepository.deleteById(id);
    }
}
