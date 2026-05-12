package com.eventos.cl.ms_eventos.controller;

import com.eventos.cl.ms_eventos.dto.EventoRequestDTO;
import com.eventos.cl.ms_eventos.dto.EventoResponseDTO;
import com.eventos.cl.ms_eventos.service.EventoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/eventos")
@RequiredArgsConstructor
public class EventoController {

    private final EventoService eventoService;

    @GetMapping
    public ResponseEntity<List<EventoResponseDTO>> listar() {
        List<EventoResponseDTO> eventos = eventoService.obtenerTodos();
        if (eventos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(eventos);
    }

    // @Valid atrapa errores de capacidad nula, nombres vacios, etc.
    @PostMapping
    public ResponseEntity<EventoResponseDTO> crear(@Valid @RequestBody EventoRequestDTO dto) {
        return ResponseEntity.status(201).body(eventoService.guardar(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventoResponseDTO> buscar(@PathVariable Long id) {
        return eventoService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventoResponseDTO> actualizar(@PathVariable Long id, @Valid @RequestBody EventoRequestDTO dto) {
        return eventoService.actualizar(id, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> borrar(@PathVariable Long id) {
        if (eventoService.obtenerPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        eventoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
