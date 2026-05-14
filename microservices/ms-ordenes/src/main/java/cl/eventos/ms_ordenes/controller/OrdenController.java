package cl.eventos.ms_ordenes.controller;

import cl.eventos.ms_ordenes.dto.OrdenRequestDTO;
import cl.eventos.ms_ordenes.model.Orden;
import cl.eventos.ms_ordenes.service.OrdenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
    public Orden obtenerPorId(@PathVariable Long id) {
        return ordenService.obtenerPorId(id);
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
    public String actualizar(@PathVariable Long id, @RequestParam String nuevoEstado) {
        ordenService.actualizarEstado(id, nuevoEstado);

        if (nuevoEstado.equalsIgnoreCase("RECHAZADO"))
        {
            return "Su pago ha sido cambiado a RECHAZADO.";
        }

        return "El estado de la orden #" + id + " ahora es: " + nuevoEstado;
    }

    // DELETE: Se elimina el registro principal de la tabla ordenes.
    // http://localhost:8085/api/ordenes/eliminar/{id}
    @DeleteMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        ordenService.eliminarOrden(id);
        return "La orden #" + id + " ha sido eliminada correctamente.";
    }

    // GET: Con esto validamos el Pago.
    // http://localhost:8085/api/ordenes/validar-pago/{ticketId}
    @GetMapping("/validar-pago/{ticketId}")
    public boolean verificarPagoTicket(@PathVariable Long ticketId) {
        return ordenService.estaPagado(ticketId);
    }
}
