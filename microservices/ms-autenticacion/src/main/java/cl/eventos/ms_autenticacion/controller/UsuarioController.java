package cl.eventos.ms_autenticacion.controller;

import cl.eventos.ms_autenticacion.dto.LoginRequestDTO;
import cl.eventos.ms_autenticacion.dto.UsuarioRegistroDTO;
import cl.eventos.ms_autenticacion.dto.UsuarioResponseDTO;
import cl.eventos.ms_autenticacion.model.Rol;
import cl.eventos.ms_autenticacion.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import cl.eventos.ms_autenticacion.config.JwtUtils;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Autenticación", description = "Endpoints de gestión de usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // POST: Endpoint para iniciar sesión y obtener el Token.(Se hace despúes de Agragar al Usuario)
    // URL: http://localhost:8081/api/auth/login

    /*
    {
        "username": "",
        "password": ""
    }
    */
    @Operation(summary = "Login de usuario", description = "Valida credenciales y retorna un JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login exitoso"),
            @ApiResponse(responseCode = "401", description = "Credenciales incorrectas")
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginRequest) {
        log.info("Intento de login para: {}", loginRequest.getUsername());

        var resultado = usuarioService.buscarPorUsernameParaAuth(loginRequest.getUsername());

        if (resultado.isPresent()) {
            var user = resultado.get();

            if (passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                String token = jwtUtils.generarToken(user.getUsername());
                return ResponseEntity.ok(java.util.Map.of("token", token));

            } else {
                
                log.warn("Contraseña incorrecta para el usuario: {}", loginRequest.getUsername());
                return ResponseEntity.status(401).build();
            }
        }

        log.warn("Usuario no encontrado: {}", loginRequest.getUsername());
        return ResponseEntity.status(401).build();
    }

    // GET: La lista de todos los Usuarios.
    // http://localhost:8081/api/auth/usuarios
    @Operation(summary = "Listar todos los Usuarios")
    @GetMapping("/usuarios")
    public ResponseEntity<List<UsuarioResponseDTO>> listar() {
        log.info("Cargando lista completa de usuarios...");
        List<UsuarioResponseDTO> usuarios = usuarioService.obtenerTodos();

        if (usuarios.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(usuarios);
    }

    // POST: Agregamos un Usuario.
    // http://localhost:8081/api/auth/registrar

    /* Para agregar un usuario

    {
        "username":"",
        "password":"",
        "email":"",
        "rol": ""(Mayuscula)
    }

    */
    @Operation(summary = "Registrar nuevo usuario")
    @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente")
    @PostMapping("/registrar")
    public ResponseEntity<UsuarioResponseDTO> registrar(@Valid @RequestBody UsuarioRegistroDTO dto) {
        log.info("Petición de registro recibida para el usuario: {}", dto.getUsername());
        UsuarioResponseDTO guardado = usuarioService.guardar(dto);

        return ResponseEntity.status(201).body(guardado);
    }

    // GET: Buscamos al Usuario por el ID.
    // http://localhost:8081/api/auth/usuario/{id}
    @Operation(summary = "Buscar Usuario por su ID")
    @GetMapping("/usuario/{id}")
    public ResponseEntity<UsuarioResponseDTO> buscar(@PathVariable Long id) {
        log.info("Petición de búsqueda para ID: {}", id);

        return usuarioService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET: Filtramos la lista de los Usuarios por su Rol.
    // http://localhost:8081/api/auth/usuario/rol/{rol}
    @Operation(summary = "Buscar Usuarios por su Rol")
    @GetMapping("/usuario/rol/{rol}")
    public ResponseEntity<List<UsuarioResponseDTO>> buscarPorRol(@PathVariable Rol rol) {
        log.info("Petición recibida para listar usuarios con el rol: {}", rol);

        List<UsuarioResponseDTO> usuarios = usuarioService.buscarPorRol(rol);

        if (usuarios.isEmpty()) {
            log.warn("No se encontraron usuarios registrados con el rol: {}", rol);
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(usuarios);
    }

    // DELETE: Borramos al Usuario por su ID.
    // http://localhost:8081/api/auth/usuario/{id}
    @Operation(summary = "Eliminar Usuario por su Id")
    @DeleteMapping("/usuario/{id}")
    public ResponseEntity<Void> borrar(@PathVariable Long id) {
        log.info("Petición para eliminar usuario con ID: {}", id);

        if (usuarioService.obtenerPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        usuarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // PUT: Actualizar Usuario
    // http://localhost:8081/api/auth/usuario/{id}
    @Operation(summary = "Actualizar un _usuario por el ID")
    @PutMapping("/usuario/{id}")
    public ResponseEntity<UsuarioResponseDTO> actualizar(@PathVariable Long id, @Valid @RequestBody UsuarioRegistroDTO dto) {
        log.info("Petición para actualizar usuario ID: {}", id);

        return usuarioService.actualizarOptional(id, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET: Filtrar por Username
    // http://localhost:8081/api/auth/usuario/buscar?username=
    @Operation(summary = "Buscar Usuario por su username")
    @GetMapping("/usuario/buscar")
    public ResponseEntity<List<UsuarioResponseDTO>> buscarPorUsername(@RequestParam String username) {
        log.info("Buscando usuarios que coincidan con: {}", username);

        List<UsuarioResponseDTO> usuarios = usuarioService.buscarPorUsername(username);

        if (usuarios.isEmpty()) {
            log.warn("No se encontraron usuarios con el filtro: {}", username);
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(usuarios);
    }

    // GET: Endpoint para que otros microservicios se validen.
    // URL: http://localhost:8081/api/auth/validar?token=(Agregar Token)
    @Operation(summary = "Validar el token del Usuario")
    @GetMapping("/validar")
    public ResponseEntity<?> validarToken(@RequestParam String token) {

        if (jwtUtils.validarToken(token)) {
            String username = jwtUtils.getNombreUsuarioDesdeToken(token);
            return ResponseEntity.ok(java.util.Map.of(
                    "username", username,
                    "valido", true,
                    "mensaje", "Token verificado correctamente"
            ));
        }
        return ResponseEntity.status(401).build();
    }
}
