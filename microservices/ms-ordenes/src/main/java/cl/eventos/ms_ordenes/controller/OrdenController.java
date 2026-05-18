package cl.eventos.ms_ordenes.controller;

import cl.eventos.ms_ordenes.dto.OrdenRequestDTO;
import cl.eventos.ms_ordenes.model.Orden;
import cl.eventos.ms_ordenes.service.OrdenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PostMapping("/crear")
    public String crear(@Valid @RequestBody OrdenRequestDTO ordenDTO, @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.replace("Bearer ", "");

        ordenService.crearOrden(ordenDTO, token);
        return "La Orden ha sido guardada exitosamente.";
    }

    // GET: Listar todas las órdenes.
    // http://localhost:8085/api/ordenes/listar
    @GetMapping("/listar")
    public List<Orden> listar() {
        return ordenService.listarTodas();
    }

    // GET: Buscar por ID.
    // http://localhost:8085/api/ordenes/{id}
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        return ordenService.obtenerPorId(id)
                .map(orden -> ResponseEntity.ok((Object) orden))
                .orElse(ResponseEntity.status(404).body("Orden no encontrada"));
    }

    // GET: Buscar órdenes por ID de Usuario.
    // http://localhost:8085/api/ordenes/filtrar/{usuarioId}
    @GetMapping("/filtrar/{usuarioId}")
    public List<Orden> filtrar(@PathVariable Long usuarioId) {
        return ordenService.obtenerPorUsuario(usuarioId);
    }

    // PUT: Actualizar el estado de una orden (PENDIENTE a PAGADO o RECHAZADO).
    // http://localhost:8085/api/ordenes/actualizar/{id}?nuevoEstado=
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
    @GetMapping("/validar-pago/{ticketId}")
    public boolean verificarPagoTicket(@PathVariable Long ticketId) {
        return ordenService.estaPagado(ticketId);
    }
}
