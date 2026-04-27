
// Recuerda utilizar la URL para el Postman: http://localhost:8084/tickets

package com.tickets.cl.ms_tickets.controller;

import com.tickets.cl.ms_tickets.dto.TicketRequestDTO;
import com.tickets.cl.ms_tickets.dto.TicketResponseDTO;
import com.tickets.cl.ms_tickets.service.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor

public class TicketController {

    private TicketService ticketService;

    @GetMapping
    public ResponseEntity<List<TicketResponseDTO>> obtenerTodos() {
        return ResponseEntity.ok(ticketService.obtenerTodos());
    }

    @GetMapping("{id}")
    public ResponseEntity<TicketResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ticketService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<TicketResponseDTO> crear(@Valid @RequestBody TicketRequestDTO dto) {
        return ResponseEntity.status(201).body(ticketService.guardar(dto));
    }

    @PutMapping("{id}")
    public ResponseEntity<TicketResponseDTO> actualizar(@PathVariable Long id, @Valid @RequestBody TicketRequestDTO dto){
        return ticketService.actualizar(id, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("{id}")
    public  ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (ticketService.obtenerPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        ticketService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<TicketResponseDTO>> buscarPorId(@RequestParam String tipo) {
        return ResponseEntity.ok(ticketService.buscarPorTipo(tipo));
    }

    @GetMapping("/categoria/{id}")
    public ResponseEntity<List<TicketResponseDTO>> buscarPorCategoria(@PathVariable Long id) {
        return ResponseEntity.ok(ticketService.buscarPorCategoria(id));
    }

    @GetMapping("/presupuesto")
    public ResponseEntity<List<TicketResponseDTO>> bajoPresupuesto(@RequestParam BigDecimal max) {
        return ResponseEntity.ok(ticketService.buscarBajoPresupuesto(max));
    }

}