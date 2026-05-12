package com.eventos.cl.ms_soporte.controller;

import com.eventos.cl.ms_soporte.dto.TicketRequestDTO;
import com.eventos.cl.ms_soporte.dto.TicketResponseDTO;
import com.eventos.cl.ms_soporte.service.TicketSoporteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/soporte")
@RequiredArgsConstructor
public class TicketSoporteController {

    private final TicketSoporteService ticketSoporteService;

    @GetMapping
    public ResponseEntity<List<TicketResponseDTO>> listar() {
        List<TicketResponseDTO> tickets = ticketSoporteService.obtenerTodos();
        if (tickets.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(tickets);
    }

    @PostMapping
    public ResponseEntity<TicketResponseDTO> crear(@Valid @RequestBody TicketRequestDTO dto) {
        return ResponseEntity.status(201).body(ticketSoporteService.guardar(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketResponseDTO> buscar(@PathVariable Long id) {
        return ticketSoporteService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Ruta especial para que los administradores actualicen el estado del ticket
    @PatchMapping("/{id}/estado")
    public ResponseEntity<TicketResponseDTO> cambiarEstado(@PathVariable Long id, @RequestParam String nuevoEstado) {
        return ticketSoporteService.actualizarEstado(id, nuevoEstado)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> borrar(@PathVariable Long id) {
        if (ticketSoporteService.obtenerPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        ticketSoporteService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
