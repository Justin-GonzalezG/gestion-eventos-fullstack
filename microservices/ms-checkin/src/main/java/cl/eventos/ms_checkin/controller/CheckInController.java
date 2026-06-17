package cl.eventos.ms_checkin.controller;

import cl.eventos.ms_checkin.dto.CheckInRequestDTO;
import cl.eventos.ms_checkin.dto.CheckInResponseDTO;
import cl.eventos.ms_checkin.service.CheckInService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/checkin")
@RequiredArgsConstructor
@Tag(name = "Check-in", description = "Endpoints para registro de ingresos")
public class CheckInController {

    private final CheckInService checkInService;

    // POST: Registrar un nuevo ingreso.
    // http://localhost:8090/api/checkin/registrar
    /*
{
    "ticketId":
}
     */
    @Operation(summary = "Registrar nuevo ingreso")
    @PostMapping("/registrar")
    public ResponseEntity<CheckInResponseDTO> registrar(@Valid @RequestBody CheckInRequestDTO dto) {
        System.out.println("Iniciando registro de ingreso para Ticket ID: " + dto.getTicketId());

        CheckInResponseDTO nuevo = checkInService.registrarIngreso(dto);

        System.out.println("Ingreso registrado exitosamente. ID Registro: " + nuevo.getId());
        return ResponseEntity.status(201).body(nuevo);
    }

    // GET: Listar todos los ingresos.
    // http://localhost:8090/api/checkin/listar
    @Operation(summary = "Listar los Ticket Chequeados")
    @GetMapping("/listar")
    public ResponseEntity<List<CheckInResponseDTO>> listar() {
        System.out.println("Obteniendo lista completa de asistentes");

        List<CheckInResponseDTO> lista = checkInService.obtenerTodos();
        if (lista.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(lista);
    }

    // GET: Buscar ingreso por Ticket ID.
    // http://localhost:8090/api/checkin/ticket/{ticketId}
    @Operation(summary = "Busacr Check-in por la ID del Ticket")
    @GetMapping("/ticket/{ticketId}")
    public ResponseEntity<CheckInResponseDTO> buscarPorTicket(@PathVariable Long ticketId) {
        System.out.println("Verificando estado de ingreso del ticket: " + ticketId);

        return checkInService.buscarPorTicket(ticketId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // PUT: Actualizar un registro de ingreso por ID.
    // http://localhost:8090/api/checkin/actualizar/{id}
    @Operation(summary = "Actualizar el Ingreso por el ID")
    @PutMapping("/actualizar/{id}")
    public ResponseEntity<CheckInResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody CheckInRequestDTO dto) {
        System.out.println("Actualizando registro de ingreso ID: " + id);

        return checkInService.actualizar(id, dto)
                .map(actualizado -> {
                    System.out.println("Registro ID: " + id + " actualizado correctamente.");
                    return ResponseEntity.ok(actualizado);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE: Eliminar un registro de ingreso.
    // http://localhost:8090/api/checkin/eliminar/{id}
    @Operation(summary = "Eliminar un Chequeo por su ID")
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        System.out.println("Eliminando registro de ingreso ID: " + id);

        if (checkInService.obtenerTodos().stream().noneMatch(c -> c.getId().equals(id))) {
            return ResponseEntity.notFound().build();
        }

        checkInService.eliminar(id);
        System.out.println("Registro eliminado exitosamente.");

        return ResponseEntity.noContent().build();
    }

    // GET: Obtener total de asistentes (Cuantos Ticket chequeados).
    // http://localhost:8090/api/checkin/aforo
    @Operation(summary = "Obtener la lista de los Tickets Chequeados")
    @GetMapping("/aforo")
    public ResponseEntity<Long> obtenerAforo() {
        Long total = checkInService.obtenerTotalAsistentes();
        System.out.println("Consulta de aforo: " + total + " personas en el evento.");
        return ResponseEntity.ok(total);
    }
}
