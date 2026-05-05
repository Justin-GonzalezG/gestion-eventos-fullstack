
// http://localhost:8081/api/auth <-- Este es el URL Benja para el Atuenticador de Usuarios

package cl.eventos.ms_autenticacion.controller;

import cl.eventos.ms_autenticacion.dto.UsuarioRegistroDTO;
import cl.eventos.ms_autenticacion.dto.UsuarioResponseDTO;
import cl.eventos.ms_autenticacion.model.Rol;
import cl.eventos.ms_autenticacion.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor

public class UsuarioController {

    private final UsuarioService usuarioService;

    // GET: La lista de todos los Usuarios.
    // http://localhost:8081/api/auth/usuario
    @GetMapping("/usuario")
    public ResponseEntity<Object> listar() {
        List<UsuarioResponseDTO> usuarios = usuarioService.obtenerTodos();
        if (usuarios.isEmpty()) {
            return ResponseEntity.status(200).body("No hay usuarios registrados actualmente.");
        }
        return ResponseEntity.ok(usuarios);
    }

    // POST: Agregamos un Usuario.
    // http://localhost:8081/api/auth/registrar
    @PostMapping("/registrar")
    public ResponseEntity<Object> registrar(@Valid @RequestBody UsuarioRegistroDTO dto) {
        UsuarioResponseDTO guardado = usuarioService.guardar(dto);

        return ResponseEntity.status(201).body(guardado);
    }

    // GET: Buscamos al Usuario por el ID del Usuario.
    // http://localhost:8081/api/auth/usuario/{id}
    @GetMapping("/usuario/{id}")
    public ResponseEntity<Object> buscar(@PathVariable Long id) {
        return usuarioService.obtenerPorId(id)
                .map(u -> ResponseEntity.ok((Object) u))
                .orElse(ResponseEntity.status(404).body("Usuario con ID " + id + " no encontrado."));
    }

    // GET: Filtramos la lista de los Usuarios por su Rol.
    // http://localhost:8081/api/auth/usuario/rol/{rol}
    @GetMapping("/usuario/rol/{rol}")
    public ResponseEntity<Object> buscarPorRol(@PathVariable Rol rol) {
        List<UsuarioResponseDTO> usuarios = usuarioService.buscarPorRol(rol);
        if (usuarios.isEmpty()) {

            return ResponseEntity.status(404).body("No se encontraron usuarios con el rol: " + rol);

        }

        return ResponseEntity.ok(usuarios);
    }

    // DELETE: Borramos al Usuario por su ID.
    // http://localhost:8081/api/auth/usuario/{id}
    @DeleteMapping("/usuario/{id}")
    public ResponseEntity<Object> borrar(@PathVariable Long id) {
        if (usuarioService.obtenerPorId(id).isEmpty()) {

            return ResponseEntity.status(404).body("No se pudo eliminar debido a que el usuario no existe.");

        }

        usuarioService.eliminar(id);
        return ResponseEntity.ok("Usuario eliminado correctamente.");
    }
}
