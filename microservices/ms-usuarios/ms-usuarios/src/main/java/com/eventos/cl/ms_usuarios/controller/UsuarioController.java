package com.eventos.cl.ms_usuarios.controller;

import com.eventos.cl.ms_usuarios.dto.UsuarioRequestDTO;
import com.eventos.cl.ms_usuarios.dto.UsuarioResponseDTO;
import com.eventos.cl.ms_usuarios.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> listar() {
        List<UsuarioResponseDTO> usuarios = usuarioService.obtenerTodos();
        if (usuarios.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204
        }
        return ResponseEntity.ok(usuarios); // 200
    }

    // El @Valid detona el chequeo del RequestDTO, y si falla salta el GlobalException!
    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> crear(@Valid @RequestBody UsuarioRequestDTO dto) {
        return ResponseEntity.status(201).body(usuarioService.guardar(dto)); // 201
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> buscar(@PathVariable Long id) {
        return usuarioService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build()); // 404
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> actualizar(@PathVariable Long id, @Valid @RequestBody UsuarioRequestDTO dto) {
        return usuarioService.actualizar(id, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build()); // 404
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> borrar(@PathVariable Long id) {
        if (usuarioService.obtenerPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build(); // 404
        }
        usuarioService.eliminar(id);
        return ResponseEntity.noContent().build(); // 204
    }
}
