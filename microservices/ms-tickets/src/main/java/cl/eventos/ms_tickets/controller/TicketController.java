package cl.eventos.ms_tickets.controller;

import cl.eventos.ms_tickets.dto.TicketRequestDTO;
import cl.eventos.ms_tickets.dto.TicketResponseDTO;
import cl.eventos.ms_tickets.service.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    // GET: http://localhost:8083/api/tickets
    @GetMapping
    public ResponseEntity<List<TicketResponseDTO>> obtenerTodos() {
        return ResponseEntity.ok(ticketService.obtenerTodos());
    }

    // GET: http://localhost:8083/api/tickets/{id}
    @GetMapping("/{id}")
    public ResponseEntity<TicketResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ticketService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST: http://localhost:8083/api/tickets/crear
    @PostMapping("/crear")
    public ResponseEntity<?> crear(@Valid @RequestBody TicketRequestDTO dto) {
        TicketResponseDTO nuevo = ticketService.guardar(dto);
        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("mensaje", "El ticket fue creado con éxito");
        respuesta.put("ticket", nuevo);
        return ResponseEntity.status(201).body(respuesta);
    }

    // PUT: http://localhost:8083/api/tickets/{id}
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @Valid @RequestBody TicketRequestDTO dto) {
        return ticketService.actualizar(id, dto)
                .map(ticketActualizado -> {
                    Map<String, Object> respuesta = new LinkedHashMap<>();
                    respuesta.put("mensaje", "El ticket fue actualizado con éxito");
                    respuesta.put("ticket", ticketActualizado);
                    return ResponseEntity.ok(respuesta);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE: http://localhost:8083/api/tickets/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        if (ticketService.obtenerPorId(id).isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("error", "El ticket con ID " + id + " no existe."));
        }
        ticketService.eliminar(id);
        Map<String, String> respuesta = new LinkedHashMap<>();
        respuesta.put("mensaje", "Ticket eliminado con éxito de la base de datos.");
        return ResponseEntity.ok(respuesta);
    }

    // GET: http://localhost:8083/api/tickets/buscar?tipo=TextoAqui
    @GetMapping("/buscar")
    public ResponseEntity<List<TicketResponseDTO>> buscarPorTipo(@RequestParam String tipo) {
        return ResponseEntity.ok(ticketService.buscarPorTipo(tipo));
    }

    // GET: http://localhost:8083/api/tickets/categoria/{id}
    @GetMapping("/categoria/{id}")
    public ResponseEntity<List<TicketResponseDTO>> buscarPorCategoria(@PathVariable Long id) {
        return ResponseEntity.ok(ticketService.buscarPorCategoria(id));
    }

    // GET: http://localhost:8083/api/tickets/presupuesto?max=50000
    @GetMapping("/presupuesto")
    public ResponseEntity<List<TicketResponseDTO>> bajoPresupuesto(@RequestParam BigDecimal max) {
        return ResponseEntity.ok(ticketService.buscarBajoPresupuesto(max));
    }
}