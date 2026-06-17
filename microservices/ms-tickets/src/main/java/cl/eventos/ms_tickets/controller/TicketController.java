package cl.eventos.ms_tickets.controller;

import cl.eventos.ms_tickets.dto.TicketRequestDTO;
import cl.eventos.ms_tickets.dto.TicketResponseDTO;
import cl.eventos.ms_tickets.service.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Tag(name = "Tickets", description = "Gestión de inventario de tickets")
@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    // GET: Muestra la lista de todos los tickets.
    // http://localhost:8084/api/tickets/listar
    @Operation(summary = "Listar todos los tickets", description = "Retorna la lista completa de tickets disponibles")
    @GetMapping("/listar")
    public ResponseEntity<List<TicketResponseDTO>> obtenerTodos() {
        return ResponseEntity.ok(ticketService.obtenerTodos());
    }

    // GET: Buscamos el ticket por su ID.
    // http://localhost:8084/api/tickets/{id}
    @Operation(summary = "Buscar ticket por ID", description = "Obtiene información detallada de un ticket específico")
    @GetMapping("/{id}")
    public ResponseEntity<TicketResponseDTO> obtenerPorId(@PathVariable Long id) {
        Optional<TicketResponseDTO> ticketOpt = ticketService.obtenerPorId(id);
        if (ticketOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ticketOpt.get());
    }

    // POST: Creamos un nuevo Ticket.
    // http://localhost:8084/api/tickets/crear
    @Operation(summary = "Crear nuevo ticket", description = "Registra un nuevo ticket en el inventario")
    @PostMapping("/crear")
    public ResponseEntity<?> crear(@Valid @RequestBody TicketRequestDTO dto) {
        TicketResponseDTO nuevo = ticketService.guardar(dto);
        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("mensaje", "El ticket fue creado con éxito");
        respuesta.put("ticket", nuevo);
        return ResponseEntity.status(201).body(respuesta);
    }

    // PUT: Actualizamos el ticket.
    // http://localhost:8084/api/tickets/{id}
    @Operation(summary = "Actualizar ticket", description = "Modifica los datos de un ticket existente")
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @Valid @RequestBody TicketRequestDTO dto) {
        Optional<TicketResponseDTO> ticketOpt = ticketService.actualizar(id, dto);
        if (ticketOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("mensaje", "El ticket fue actualizado con éxito");
        respuesta.put("ticket", ticketOpt.get());
        return ResponseEntity.ok(respuesta);
    }

    // DELETE: Borramos el Ticket usando la ID.
    // http://localhost:8084/api/tickets/{id}
    @Operation(summary = "Eliminar ticket", description = "Elimina un ticket permanentemente")
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

    // GET: Filtramos el Ticket por Tipo de Evento.
    // http://localhost:8084/api/tickets/buscar?tipo=TextoAqui
    @Operation(summary = "Buscar por tipo", description = "Filtra tickets según el tipo de evento")
    @GetMapping("/buscar")
    public ResponseEntity<List<TicketResponseDTO>> buscarPorTipo(@RequestParam String tipo) {
        return ResponseEntity.ok(ticketService.buscarPorTipo(tipo));
    }

    // GET: Filtramos el Ticket por Categoria.
    // http://localhost:8084/api/tickets/categoria/{id}
    @Operation(summary = "Buscar por categoría", description = "Obtiene tickets asociados a una categoría específica")
    @GetMapping("/categoria/{id}")
    public ResponseEntity<List<TicketResponseDTO>> buscarPorCategoria(@PathVariable Long id) {
        return ResponseEntity.ok(ticketService.buscarPorCategoria(id));
    }

    // GET: Filtramos el Ticket por Presupuesto maximo.
    // http://localhost:8084/api/tickets/presupuesto?max=
    @Operation(summary = "Buscar bajo presupuesto", description = "Filtra tickets con precio menor o igual al máximo indicado")
    @GetMapping("/presupuesto")
    public ResponseEntity<List<TicketResponseDTO>> bajoPresupuesto(@RequestParam BigDecimal max) {
        return ResponseEntity.ok(ticketService.buscarBajoPresupuesto(max));
    }

    // PUT: Actualiza solo el stock del Ticket (usado por ms-ordenes)
    // http://localhost:8084/api/tickets/{id}/stock?nuevoStock=ValorAqui
    @Operation(summary = "Actualizar stock", description = "Modifica únicamente la cantidad de stock disponible")
    @PutMapping("/{id}/stock")
    public ResponseEntity<?> actualizarStock(@PathVariable Long id, @RequestParam Integer nuevoStock) {
        ticketService.actualizarSoloStock(id, nuevoStock);
        return ResponseEntity.ok(Map.of("mensaje", "Stock actualizado a: " + nuevoStock));
    }
}
