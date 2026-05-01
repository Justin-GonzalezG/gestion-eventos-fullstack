package cl.eventos.ms_tickets.controller;

import cl.eventos.ms_tickets.model.Categoria;
import cl.eventos.ms_tickets.service.CategoriaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/categorias")
@RequiredArgsConstructor
public class CategoriaController {

    private final CategoriaService categoriaService;

    // GET: http://localhost:8083/api/categorias
    @GetMapping
    public ResponseEntity<List<Categoria>> obtenerTodas() {
        return ResponseEntity.ok(categoriaService.obtenerTodas());
    }

    // GET: http://localhost:8083/api/categorias/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Categoria> obtenerPorId(@PathVariable Long id) {
        return categoriaService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST: http://localhost:8083/api/categorias
    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody Categoria categoria) {
        Categoria nueva = categoriaService.guardar(categoria);
        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("mensaje", "La categoría fue creada con éxito");
        respuesta.put("categoria", nueva);
        return ResponseEntity.status(201).body(respuesta);
    }

    // PUT: http://localhost:8083/api/categorias/{id}
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @Valid @RequestBody Categoria datos) {
        return categoriaService.obtenerPorId(id)
                .map(existente -> {
                    datos.setId(id);
                    Categoria actualizada = categoriaService.guardar(datos);
                    Map<String, Object> respuesta = new LinkedHashMap<>();
                    respuesta.put("mensaje", "Categoría actualizada correctamente");
                    respuesta.put("categoria", actualizada);
                    return ResponseEntity.ok(respuesta);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE: http://localhost:8083/api/categorias/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        if (categoriaService.obtenerPorId(id).isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("error", "No se encontró la categoría con ID: " + id));
        }
        categoriaService.eliminar(id);
        Map<String, String> respuesta = new LinkedHashMap<>();
        respuesta.put("mensaje", "Categoría eliminada correctamente.");
        return ResponseEntity.ok(respuesta);
    }
}