package com.eventos.cl.ms_reportes.controller;

import com.eventos.cl.ms_reportes.dto.ReporteRequestDTO;
import com.eventos.cl.ms_reportes.dto.ReporteResponseDTO;
import com.eventos.cl.ms_reportes.service.ReporteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private final ReporteService reporteService;

    @GetMapping
    public ResponseEntity<List<ReporteResponseDTO>> listar() {
        List<ReporteResponseDTO> reportes = reporteService.obtenerTodos();
        if (reportes.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(reportes);
    }

    @PostMapping
    public ResponseEntity<ReporteResponseDTO> crear(@Valid @RequestBody ReporteRequestDTO dto) {
        return ResponseEntity.status(201).body(reporteService.guardar(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReporteResponseDTO> buscar(@PathVariable Long id) {
        return reporteService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReporteResponseDTO> actualizar(@PathVariable Long id, @Valid @RequestBody ReporteRequestDTO dto) {
        return reporteService.actualizar(id, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> borrar(@PathVariable Long id) {
        if (reporteService.obtenerPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        reporteService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
