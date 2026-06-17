package cl.eventos.ms_soporte.controller;

import cl.eventos.ms_soporte.dto.TicketConDetalleDTO;
import cl.eventos.ms_soporte.dto.TicketRequestDTO;
import cl.eventos.ms_soporte.dto.TicketResponseDTO;
import cl.eventos.ms_soporte.service.TicketSoporteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Soporte", description = "Gestión de tickets de ayuda e incidencias")
@RestController
@RequestMapping("/api/soporte")
@RequiredArgsConstructor
public class TicketSoporteController {

    private final TicketSoporteService ticketSoporteService;

    @Operation(summary = "Listar todos los tickets")
    @GetMapping
    public ResponseEntity<List<TicketResponseDTO>> listar() {
        List<TicketResponseDTO> tickets = ticketSoporteService.obtenerTodos();
        if (tickets.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(tickets);
    }

    @Operation(summary = "Crear nuevo ticket")
    @PostMapping
    public ResponseEntity<TicketResponseDTO> crear(@Valid @RequestBody TicketRequestDTO dto) {
        return ResponseEntity.status(201).body(ticketSoporteService.guardar(dto));
    }

    @Operation(summary = "Buscar ticket por ID")
    @GetMapping("/{id}")
    public ResponseEntity<TicketResponseDTO> buscar(@PathVariable Long id) {
        return ticketSoporteService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Obtener detalle completo (Ticket + Usuario + Orden)")
    @GetMapping("/{id}/detalle")
    public ResponseEntity<TicketConDetalleDTO> buscarConDetalle(@PathVariable Long id) {
        return ticketSoporteService.obtenerDetalleCompleto(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Actualizar estado del ticket")
    @PatchMapping("/{id}/estado")
    public ResponseEntity<TicketResponseDTO> cambiarEstado(@PathVariable Long id, @RequestParam String nuevoEstado) {
        return ticketSoporteService.actualizarEstado(id, nuevoEstado)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Eliminar ticket")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> borrar(@PathVariable Long id) {
        if (ticketSoporteService.obtenerPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        ticketSoporteService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
