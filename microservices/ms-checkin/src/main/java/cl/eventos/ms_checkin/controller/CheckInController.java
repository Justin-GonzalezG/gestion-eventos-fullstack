package cl.eventos.ms_checkin.controller;

import cl.eventos.ms_checkin.dto.CheckInRequestDTO;
import cl.eventos.ms_checkin.dto.CheckInResponseDTO;
import cl.eventos.ms_checkin.service.CheckInService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/checkin")
@RequiredArgsConstructor

public class CheckInController {

    private final CheckInService checkInService;

    // POST: Registrar un nuevo ingreso.
    // http://localhost:8089/api/checkin/registrar
    @PostMapping("/registrar")
    public ResponseEntity<?> registrar(@Valid @RequestBody CheckInRequestDTO dto) {
        System.out.println("Iniciando registro de ingreso para Ticket ID: " + dto.getTicketId());

        CheckInResponseDTO nuevo = checkInService.registrarIngreso(dto);

        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("mensaje", "El ingreso ha sido registrado exitosamente");
        respuesta.put("checkin", nuevo);

        System.out.println("Ingreso registrado exitosamente. ID Registro: " + nuevo.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    // GET: Listar todos los ingresos.
    // http://localhost:8089/api/checkin/listar
    @GetMapping("/listar")
    public ResponseEntity<List<CheckInResponseDTO>> listar() {
        System.out.println("Obteniendo lista completa de asistentes");
        return ResponseEntity.ok(checkInService.obtenerTodos());
    }

    // GET: Buscar ingreso por Ticket ID.
    // http://localhost:8089/api/checkin/ticket/{ticketId}
    @GetMapping("/ticket/{ticketId}")
    public ResponseEntity<CheckInResponseDTO> buscarPorTicket(@PathVariable Long ticketId) {
        System.out.println("Verificando estado de ingreso del ticket: " + ticketId);
        return checkInService.buscarPorTicket(ticketId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // PUT: Actualizar un registro de ingreso por ID.
    // http://localhost:8089/api/checkin/actualizar/{id}
    @PutMapping("/actualizar/{id}")
    public ResponseEntity<?> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody CheckInRequestDTO dto) {
        System.out.println("Actualizando registro de ingreso ID: " + id);

        return checkInService.actualizar(id, dto)
                .map(actualizado -> {

                    Map<String, Object> respuesta = new LinkedHashMap<>();
                    respuesta.put("mensaje", "El registro de ingreso fue actualizado con éxito");
                    respuesta.put("checkin", actualizado);
                    System.out.println("Registro ID: " + id + " actualizado correctamente.");

                    return ResponseEntity.ok(respuesta);

                })
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE: Eliminar un registro de ingreso.
    // http://localhost:8089/api/checkin/eliminar/{id}
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        System.out.println("Eliminando registro de ingreso ID: " + id);

        if (checkInService.obtenerTodos().stream().noneMatch(c -> c.getId().equals(id))) {
            return ResponseEntity.status(404).body(Map.of("error", "El registro con ID " + id + " no existe."));
        }

        checkInService.eliminar(id);

        Map<String, String> respuesta = new LinkedHashMap<>();
        respuesta.put("mensaje", "Registro de ingreso eliminado con éxito de la base de datos.");
        System.out.println("Registro eliminado exitosamente.");

        return ResponseEntity.ok(respuesta);
    }

    // GET: Obtener total de asistentes (Cuantos Ticket chequeados).
    // http://localhost:8089/api/checkin/aforo
    @GetMapping("/aforo")
    public ResponseEntity<Long> obtenerAforo() {
        Long total = checkInService.obtenerTotalAsistentes();
        System.out.println("Consulta de aforo: " + total + " personas en el evento.");
        return ResponseEntity.ok(total);
    }
}