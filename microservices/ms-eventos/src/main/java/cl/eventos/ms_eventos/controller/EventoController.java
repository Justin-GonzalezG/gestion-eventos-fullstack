package cl.eventos.ms_eventos.controller;

import cl.eventos.ms_eventos.dto.EventoRequestDTO;
import cl.eventos.ms_eventos.dto.EventoResponseDTO;
import cl.eventos.ms_eventos.service.EventoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Eventos", description = "Endpoints de gestion de Eventos")
@RestController
@RequestMapping("/api/eventos")
@RequiredArgsConstructor
public class EventoController {

    private final EventoService eventoService;

    @Operation(summary = "Listar todos los Eventos")
    @GetMapping
    public ResponseEntity<List<EventoResponseDTO>> listar() {
        List<EventoResponseDTO> eventos = eventoService.obtenerTodos();
        if (eventos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(eventos);
    }

    @Operation(summary = "Crear un nuevo Evento")
    // @Valid atrapa errores de capacidad nula, nombres vacios, etc.
    @PostMapping
    public ResponseEntity<EventoResponseDTO> crear(@Valid @RequestBody EventoRequestDTO dto) {
        return ResponseEntity.status(201).body(eventoService.guardar(dto));
    }

    @Operation(summary = "Buscar Evento por ID")
    @GetMapping("/{id}")
    public ResponseEntity<EventoResponseDTO> buscar(@PathVariable Long id) {
        return eventoService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Actualizar un Evento por ID")
    @PutMapping("/{id}")
    public ResponseEntity<EventoResponseDTO> actualizar(@PathVariable Long id, @Valid @RequestBody EventoRequestDTO dto) {
        return eventoService.actualizar(id, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Eliminar un Evento por ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> borrar(@PathVariable Long id) {
        if (eventoService.obtenerPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        eventoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
