package com.eventos.cl.ms_recomendacion.controller;

import com.eventos.cl.ms_recomendacion.dto.RecomendacionConDetalleDTO;
import com.eventos.cl.ms_recomendacion.dto.RecomendacionRequestDTO;
import com.eventos.cl.ms_recomendacion.dto.RecomendacionResponseDTO;
import com.eventos.cl.ms_recomendacion.service.RecomendacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recomendaciones")
@RequiredArgsConstructor
public class RecomendacionController {

    private final RecomendacionService recomendacionService;

    @GetMapping
    public ResponseEntity<List<RecomendacionResponseDTO>> listar() {
        List<RecomendacionResponseDTO> recomendaciones = recomendacionService.obtenerTodas();
        if (recomendaciones.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(recomendaciones);
    }

    @PostMapping
    public ResponseEntity<RecomendacionResponseDTO> crear(@Valid @RequestBody RecomendacionRequestDTO dto) {
        return ResponseEntity.status(201).body(recomendacionService.guardar(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecomendacionResponseDTO> buscar(@PathVariable Long id) {
        return recomendacionService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<RecomendacionResponseDTO> actualizar(@PathVariable Long id, @Valid @RequestBody RecomendacionRequestDTO dto) {
        return recomendacionService.actualizar(id, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> borrar(@PathVariable Long id) {
        if (recomendacionService.obtenerPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        recomendacionService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // Esta ruta GET devuelve un JSON combinado de Recomendaciones + Eventos.
    // Llama al servicio interno que por debajo utiliza OpenFeign para conectarse al ms-eventos.
    @GetMapping("/{id}/detalle")
    public ResponseEntity<RecomendacionConDetalleDTO> obtenerDetalle(@PathVariable Long id) {
        return recomendacionService.obtenerDetalleCompleto(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
