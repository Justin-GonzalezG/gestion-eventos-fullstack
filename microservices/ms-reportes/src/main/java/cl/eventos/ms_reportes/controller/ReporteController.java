package cl.eventos.ms_reportes.controller;

import cl.eventos.ms_reportes.dto.ReporteRequestDTO;
import cl.eventos.ms_reportes.dto.ReporteResponseDTO;
import cl.eventos.ms_reportes.service.ReporteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Reportes", description = "Generación de estadísticas y reportes del sistema")
@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private final ReporteService reporteService;

    @Operation(summary = "Listar todos los reportes")
    @GetMapping
    public ResponseEntity<List<ReporteResponseDTO>> listar() {
        List<ReporteResponseDTO> reportes = reporteService.obtenerTodos();
        if (reportes.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(reportes);
    }

    @Operation(summary = "Crear nuevo reporte")
    @PostMapping
    public ResponseEntity<ReporteResponseDTO> crear(@Valid @RequestBody ReporteRequestDTO dto) {
        return ResponseEntity.status(201).body(reporteService.guardar(dto));
    }

    @Operation(summary = "Buscar reporte por ID")
    @GetMapping("/{id}")
    public ResponseEntity<ReporteResponseDTO> buscar(@PathVariable Long id) {
        return reporteService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Actualizar reporte existente")
    @PutMapping("/{id}")
    public ResponseEntity<ReporteResponseDTO> actualizar(@PathVariable Long id, @Valid @RequestBody ReporteRequestDTO dto) {
        return reporteService.actualizar(id, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Eliminar reporte")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> borrar(@PathVariable Long id) {
        if (reporteService.obtenerPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        reporteService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
