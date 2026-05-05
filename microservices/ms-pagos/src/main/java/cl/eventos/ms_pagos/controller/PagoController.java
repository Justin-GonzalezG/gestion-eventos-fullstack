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

@RestController
@RequestMapping("/api/pagos")
@RequiredArgsConstructor

public class PagoController {

    private final PagoService pagoService;

    // POST: Crear el pago.
    // http://localhost:8086/api/pagos/crear
    @PostMapping("/crear")
    public ResponseEntity<?> crear(@RequestBody PagoRequestDTO pagoRequestDTO) {
        System.out.println("Iniciando proceso de creación de pago...");
        PagoResponseDTO response = pagoService.save(pagoRequestDTO);

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
    public ResponseEntity<PagoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(pagoService.findById(id));
    }

    // PUT: Actualizar el pago.
    // http://localhost:8080/api/pagos/actualizar/{id}
    @PutMapping("/actualizar/{id}")
    public ResponseEntity<PagoResponseDTO> actualizar(@PathVariable Long id, @RequestBody PagoRequestDTO pagoRequestDTO) {
        System.out.println("Actualizando datos del pago con ID: " + id);
        PagoResponseDTO response = pagoService.update(id, pagoRequestDTO);
        System.out.println("Pago con ID: " + id + " actualizado correctamente.");
        return ResponseEntity.ok(response);
    }

    // DELETE: Actualizar el pago.
    // http://localhost:8086/api/pagos/eliminar/{id}
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        System.out.println("Solicitud para eliminar pago con ID: " + id);

        pagoService.delete(id);

        Map<String, String> respuesta = new LinkedHashMap<>();
        respuesta.put("mensaje", "El registro de pago ha sido eliminado correctamente de la base de datos.");

        System.out.println("Pago con ID: " + id + " eliminado con éxito.");
        return ResponseEntity.ok(respuesta);
    }
}