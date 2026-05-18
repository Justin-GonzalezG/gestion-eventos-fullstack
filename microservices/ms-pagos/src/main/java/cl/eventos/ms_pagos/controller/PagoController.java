package cl.eventos.ms_pagos.controller;

import cl.eventos.ms_pagos.dto.PagoRequestDTO;
import cl.eventos.ms_pagos.dto.PagoResponseDTO;
import cl.eventos.ms_pagos.service.PagoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/pagos")
@RequiredArgsConstructor
public class PagoController {

    private final PagoService pagoService;

    // POST: Crear el pago.
    // http://localhost:8086/api/pagos/crear
    /*
{
    "ordenId": ,
    "monto": ,
    "metodoPago": ""
}
     */
    @PostMapping("/crear")
    public ResponseEntity<?> crear(@RequestBody PagoRequestDTO pagoRequestDTO, @RequestHeader("Authorization") String authHeader) {

        System.out.println("Iniciando proceso de creación de pago...");
        String token = authHeader.replace("Bearer ", "");

        PagoResponseDTO response = pagoService.save(pagoRequestDTO, token);

        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("mensaje", "Pago procesado exitosamente");
        respuesta.put("pago", response);

        return new ResponseEntity<>(respuesta, HttpStatus.CREATED);
    }

    // GET: Listar los pagos.
    // http://localhost:8086/api/pagos/listar
    @GetMapping("/listar")
    public ResponseEntity<List<PagoResponseDTO>> listar() {
        return ResponseEntity.ok(pagoService.findAll());
    }

    // GET: Buscar por ID.
    // http://localhost:8086/api/pagos/{id}
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        Optional<PagoResponseDTO> response = pagoService.findByIdOptional(id);

        if (!response.isPresent()) {
            return ResponseEntity.status(404).body("Pago no encontrado con el ID: " + id);
        }

        return ResponseEntity.ok(response.get());
    }

    // PUT: Actualizar el pago.
    // http://localhost:8086/api/pagos/actualizar/{id}
    @PutMapping("/actualizar/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody PagoRequestDTO pagoRequestDTO) {
        System.out.println("Actualizando datos del pago con ID: " + id);

        Optional<PagoResponseDTO> response = pagoService.updateOptional(id, pagoRequestDTO);

        if (!response.isPresent()) {
            return ResponseEntity.status(404).body("No se pudo actualizar porque el pago no existe.");
        }

        System.out.println("Pago con ID: " + id + " actualizado correctamente.");
        return ResponseEntity.ok(response.get());
    }

    // PUT: Actualizar solo el estado del pago (PENDIENTE, APROBADO, RECHAZADO)
    // http://localhost:8086/api/pagos/actualizar-estado/1?nuevoEstado=
    @PatchMapping("/actualizar-estado/{id}")
    public ResponseEntity<?> actualizarEstado(@PathVariable Long id, @RequestParam String nuevoEstado) {
        System.out.println("Cambiando estado del Pago ID: " + id + " a " + nuevoEstado);

        Optional<PagoResponseDTO> response = pagoService.actualizarEstadoOptional(id, nuevoEstado);

        if (!response.isPresent()) {
            return ResponseEntity.status(404).body("No se encontró el registro de pago para cambiar su estado.");
        }

        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("mensaje", "Estado de pago actualizado correctamente");
        respuesta.put("pago", response.get());

        return ResponseEntity.ok(respuesta);
    }

    // DELETE: Eliminar el historial del Pago.
    // http://localhost:8086/api/pagos/eliminar/{id}
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        System.out.println("Solicitud para eliminar pago con ID: " + id);

        if (!pagoService.existsById(id)) {
            return ResponseEntity.status(404).body("El registro de pago no existe en la base de datos.");
        }

        pagoService.delete(id);

        Map<String, String> respuesta = new LinkedHashMap<>();
        respuesta.put("mensaje", "El registro de pago ha sido eliminado correctamente de la base de datos.");

        System.out.println("Pago con ID: " + id + " eliminado con éxito.");
        return ResponseEntity.ok(respuesta);
    }
}
