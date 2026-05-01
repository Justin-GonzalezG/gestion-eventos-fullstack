package com.eventos.cl.ms_ordenes.controller;

import com.eventos.cl.ms_ordenes.model.Orden;
import com.eventos.cl.ms_ordenes.service.OrdenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ordenes")
@RequiredArgsConstructor
public class OrdenController {

    private final OrdenService ordenService;

    @PostMapping
    public ResponseEntity<Orden> crear(@RequestBody Orden orden) {
        return ResponseEntity.ok(ordenService.crearOrden(orden));
    }

    @GetMapping
    public List<Orden> listar() {
        return ordenService.listarTodas();
    }

    @GetMapping("/usuario/{usuarioId}")
    public List<Orden> porUsuario(@PathVariable Long usuarioId) {
        return ordenService.obtenerPorUsuario(usuarioId);
    }
}