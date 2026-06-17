package cl.eventos.ms_ordenes.controller;

import cl.eventos.ms_ordenes.dto.OrdenRequestDTO;
import cl.eventos.ms_ordenes.model.Orden;
import cl.eventos.ms_ordenes.service.OrdenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Órdenes", description = "Gestión de transacciones y pagos de órdenes")
@RestController
@RequestMapping("/api/ordenes")
@RequiredArgsConstructor
public class OrdenController {

    private final OrdenService ordenService;

    // POST: Crear una nueva orden.
    // http://localhost:8085/api/ordenes/crear
    /*
    {
        "usuarioId": ,
        "detalles": [
            {
                "ticketId": ,
                "cantidad":
            }
        ]
    }
    */
    @Operation(summary = "Crear orden", description = "Valida usuario, existencia de tickets y stock antes de persistir.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orden creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Error de validación o stock insuficiente")
    })
    @PostMapping("/crear")
    public String crear(@Valid @RequestBody OrdenRequestDTO ordenDTO, @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.replace("Bearer ", "");

        ordenService.crearOrden(ordenDTO, token);
        return "La Orden ha sido guardada exitosamente.";
    }

    // GET: Listar todas las órdenes.
    // http://localhost:8085/api/ordenes/listar
    @Operation(summary = "Listar todas las Ordenes", description = "Nos da una lista de todas las Ordenes creadas y guardadas.")
    @GetMapping("/listar")
    public List<Orden> listar() {
        return ordenService.listarTodas();
    }

    // GET: Buscar por ID.
    // http://localhost:8085/api/ordenes/{id}
    @Operation(summary = "Buscar Orden por ID", description = "Buscamos las Ordenes Guardadas por el ID.")
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        return ordenService.obtenerPorId(id)
                .map(orden -> ResponseEntity.ok((Object) orden))
                .orElse(ResponseEntity.status(404).body("Orden no encontrada"));
    }

    // GET: Buscar órdenes por ID de Usuario.
    // http://localhost:8085/api/ordenes/filtrar/{usuarioId}
    @Operation(summary = "Buscar Ordenes por ID Usuarios", description = "Buscamos las Ordenes por el ID del Usuario de la Orden.")
    @GetMapping("/filtrar/{usuarioId}")
    public List<Orden> filtrar(@PathVariable Long usuarioId) {
        return ordenService.obtenerPorUsuario(usuarioId);
    }

    // PUT: Actualizar el estado de una orden (PENDIENTE a PAGADO o RECHAZADO).
    // http://localhost:8085/api/ordenes/actualizar/{id}?nuevoEstado=
    @Operation(summary = "ACtualizamos el Estado de la Orden", description = "Actualizamos el Estado de la Orden manualmente por el PENDIENTE a PAGADA O RECHAZADA.")
    @PutMapping("/actualizar/{id}")
    public ResponseEntity<String> actualizar(@PathVariable Long id, @RequestParam String nuevoEstado) {
        return ordenService.actualizarEstado(id, nuevoEstado)
                .map(orden -> {
                    if (nuevoEstado.equalsIgnoreCase("RECHAZADO")) {
                        return ResponseEntity.ok("Su pago ha sido cambiado a RECHAZADO.");
                    }
                    return ResponseEntity.ok("El estado de la orden #" + id + " ahora es: " + nuevoEstado);
                })
                .orElse(ResponseEntity.status(404).body("Orden no encontrada para actualizar"));
    }

    // DELETE: Se elimina el registro principal de la tabla ordenes.
    // http://localhost:8085/api/ordenes/eliminar/{id}
    @Operation(summary = "Elimanr Orden por el ID", description = "Eliminamos la Orden Guardada por la ID de la Orden.")
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        if (ordenService.obtenerPorId(id).isEmpty()) {
            return ResponseEntity.status(404).body("La orden con ID " + id + " no existe.");
        }
        ordenService.eliminarOrden(id);
        return ResponseEntity.ok("La orden #" + id + " ha sido eliminada correctamente.");
    }

    // GET: Con esto validamos el Pago.
    // http://localhost:8085/api/ordenes/validar-pago/{ticketId}
    @Operation(summary = "Validar la Orden por su Estado", description = "Validamos la Orden y verificamos el estado de la Orden.")
    @GetMapping("/validar-pago/{ticketId}")
    public boolean verificarPagoTicket(@PathVariable Long ticketId) {
        return ordenService.estaPagado(ticketId);
    }
}
