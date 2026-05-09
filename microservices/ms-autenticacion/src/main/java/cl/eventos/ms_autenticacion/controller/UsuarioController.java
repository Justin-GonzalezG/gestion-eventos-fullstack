package cl.eventos.ms_autenticacion.controller;

import cl.eventos.ms_autenticacion.config.SecurityConfig;
import cl.eventos.ms_autenticacion.dto.LoginRequestDTO;
import cl.eventos.ms_autenticacion.dto.UsuarioRegistroDTO;
import cl.eventos.ms_autenticacion.dto.UsuarioResponseDTO;
import cl.eventos.ms_autenticacion.model.Rol;
import cl.eventos.ms_autenticacion.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import cl.eventos.ms_autenticacion.config.JwtUtils;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j

public class UsuarioController {

    private final UsuarioService usuarioService;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // POST: Endpoint para iniciar sesión y obtener el Token
    // URL: http://localhost:8081/api/auth/login
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginRequest) {
        log.info("Intento de login para: {}", loginRequest.getUsername());

        return usuarioService.buscarPorUsernameParaAuth(loginRequest.getUsername())
                .map(user -> {
                    if (passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                        String token = jwtUtils.generarToken(user.getUsername());
                        // Devolvemos el token en un mapa para que sea un objeto JSON válido
                        return ResponseEntity.ok(java.util.Map.of("token", token));
                    }
                    return ResponseEntity.status(401).body("Credenciales inválidas.");
                })
                .orElse(ResponseEntity.status(401).body("Usuario no encontrado."));
    }

    // GET: La lista de todos los Usuarios.
    // http://localhost:8081/api/auth/usuarios
    @GetMapping("/usuarios")
    public ResponseEntity<Object> listar() {
        log.info("Cargando lista completa de usuarios...");
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
        log.info("Petición de registro recibida para el usuario: {}", dto.getUsername());
        UsuarioResponseDTO guardado = usuarioService.guardar(dto);

        return ResponseEntity.status(201).body(guardado);
    }

    /* Para agregar un usuario

    {
        "username":
        "password":
        "email":
        "rol": (Mayuscula)
    }

    */

    // GET: Buscamos al Usuario por el ID del Usuario.
    // http://localhost:8081/api/auth/usuario/{id}
    @GetMapping("/usuario/{id}")
    public ResponseEntity<Object> buscar(@PathVariable Long id) {
        log.info("Petición de búsqueda para ID: {}", id);

        return usuarioService.obtenerPorId(id)
                .map(u -> ResponseEntity.ok((Object) u))
                .orElse(ResponseEntity.status(404).body("Usuario con ID " + id + " no encontrado."));
    }

    // GET: Filtramos la lista de los Usuarios por su Rol.
    // http://localhost:8081/api/auth/usuario/rol/{rol}
    @GetMapping("/usuario/rol/{rol}")
    public ResponseEntity<Object> buscarPorRol(@PathVariable Rol rol) {
        log.info("Petición recibida para listar usuarios con el rol: {}", rol);

        List<UsuarioResponseDTO> usuarios = usuarioService.buscarPorRol(rol);

        if (usuarios.isEmpty()) {

            log.warn("No se encontraron usuarios registrados con el rol: {}", rol);
            return ResponseEntity.status(404).body("No se encontraron usuarios con el rol: " + rol);
        }

        return ResponseEntity.ok(usuarios);
    }

    // DELETE: Borramos al Usuario por su ID.
    // http://localhost:8081/api/auth/usuario/{id}
    @DeleteMapping("/usuario/{id}")
    public ResponseEntity<Object> borrar(@PathVariable Long id) {
        log.info("Petición para eliminar usuario con ID: {}", id);

        if (usuarioService.obtenerPorId(id).isEmpty()) {
            return ResponseEntity.status(404).body("No se pudo eliminar debido a que el usuario no existe.");
        }

        usuarioService.eliminar(id);
        return ResponseEntity.ok("Usuario eliminado correctamente.");
    }

    // PUT: Actualizar Usuario
    // http://localhost:8081/api/auth/usuario/{id}
    @PutMapping("/usuario/{id}")
    public ResponseEntity<Object> actualizar(@PathVariable Long id, @Valid @RequestBody UsuarioRegistroDTO dto) {
        log.info("Petición para actualizar usuario ID: {}", id);

        try {

            UsuarioResponseDTO actualizado = usuarioService.actualizar(id, dto);
            return ResponseEntity.ok(actualizado);

        } catch (RuntimeException e) {

            log.error("Error al actualizar usuario: {}", e.getMessage());
            return ResponseEntity.status(404).body(e.getMessage());

        }
    }

    // GET: Filtrar por Username
    // http://localhost:8081/api/auth/usuario/buscar?username=
    @GetMapping("/usuario/buscar")
    public ResponseEntity<Object> buscarPorUsername(@RequestParam String username) {
        log.info("Buscando usuarios que coincidan con: {}", username);

        List<UsuarioResponseDTO> usuarios = usuarioService.buscarPorUsername(username);

        if (usuarios.isEmpty()) {

            log.warn("No se encontraron usuarios con el filtro: {}", username);
            return ResponseEntity.status(404).body("No se encontraron coincidencias para: " + username);
        }

        return ResponseEntity.ok(usuarios);
    }
}
