package cl.eventos.ms_autenticacion.controller;

import cl.eventos.ms_autenticacion.config.JwtUtils;
import cl.eventos.ms_autenticacion.dto.LoginRequestDTO;
import cl.eventos.ms_autenticacion.dto.UsuarioRegistroDTO;
import cl.eventos.ms_autenticacion.dto.UsuarioResponseDTO;
import cl.eventos.ms_autenticacion.model.Rol;
import cl.eventos.ms_autenticacion.model.Usuario;
import cl.eventos.ms_autenticacion.service.UsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.test.context.ActiveProfiles;

@WebMvcTest(value = UsuarioController.class, excludeAutoConfiguration = {SecurityAutoConfiguration.class})
@ActiveProfiles("test")
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioService usuarioService;

    @MockBean
    private JwtUtils jwtUtils;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private UsuarioResponseDTO usuarioResponseDTO;
    private UsuarioRegistroDTO usuarioRegistroDTO;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuarioResponseDTO = new UsuarioResponseDTO(1L, "testuser", "test@test.com", Rol.CLIENTE, true);

        usuarioRegistroDTO = new UsuarioRegistroDTO();
        usuarioRegistroDTO.setUsername("testuser");
        usuarioRegistroDTO.setPassword("rawPassword");
        usuarioRegistroDTO.setEmail("test@test.com");
        usuarioRegistroDTO.setRol(Rol.CLIENTE);

        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("testuser");
        usuario.setPassword("encodedPassword");
        usuario.setEmail("test@test.com");
        usuario.setRol(Rol.CLIENTE);
        usuario.setActivo(true);
    }

    @Test
    void login_cuandoCredencialesSonCorrectas_deberiaRetornarToken() throws Exception {
        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("rawPassword");

        when(usuarioService.buscarPorUsernameParaAuth("testuser")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("rawPassword", "encodedPassword")).thenReturn(true);
        when(jwtUtils.generarToken("testuser")).thenReturn("dummy-jwt-token");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("dummy-jwt-token"));
    }

    @Test
    void login_cuandoPasswordEsIncorrecta_deberiaRetornar401() throws Exception {
        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("wrongPassword");

        when(usuarioService.buscarPorUsernameParaAuth("testuser")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_cuandoUsuarioNoExiste_deberiaRetornar401() throws Exception {
        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setUsername("notfound");
        loginRequest.setPassword("rawPassword");

        when(usuarioService.buscarPorUsernameParaAuth("notfound")).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void listar_deberiaRetornarListaDeUsuarios() throws Exception {
        when(usuarioService.obtenerTodos()).thenReturn(Arrays.asList(usuarioResponseDTO));

        mockMvc.perform(get("/api/auth/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("testuser"));
    }

    @Test
    void listar_cuandoNoHayUsuarios_deberiaRetornar204() throws Exception {
        when(usuarioService.obtenerTodos()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/auth/usuarios"))
                .andExpect(status().isNoContent());
    }

    @Test
    void registrar_deberiaRetornar201() throws Exception {
        when(usuarioService.guardar(any(UsuarioRegistroDTO.class))).thenReturn(usuarioResponseDTO);

        mockMvc.perform(post("/api/auth/registrar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuarioRegistroDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    void buscar_cuandoExiste_deberiaRetornarUsuario() throws Exception {
        when(usuarioService.obtenerPorId(1L)).thenReturn(Optional.of(usuarioResponseDTO));

        mockMvc.perform(get("/api/auth/usuario/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    void buscar_cuandoNoExiste_deberiaRetornar404() throws Exception {
        when(usuarioService.obtenerPorId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/auth/usuario/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void buscarPorRol_cuandoExisten_deberiaRetornarLista() throws Exception {
        when(usuarioService.buscarPorRol(Rol.CLIENTE)).thenReturn(Arrays.asList(usuarioResponseDTO));

        mockMvc.perform(get("/api/auth/usuario/rol/CLIENTE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].rol").value("CLIENTE"));
    }

    @Test
    void borrar_cuandoExiste_deberiaRetornar204() throws Exception {
        when(usuarioService.obtenerPorId(1L)).thenReturn(Optional.of(usuarioResponseDTO));
        doNothing().when(usuarioService).eliminar(1L);

        mockMvc.perform(delete("/api/auth/usuario/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void borrar_cuandoNoExiste_deberiaRetornar404() throws Exception {
        when(usuarioService.obtenerPorId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/auth/usuario/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void actualizar_cuandoExiste_deberiaRetornar200() throws Exception {
        when(usuarioService.actualizarOptional(eq(1L), any(UsuarioRegistroDTO.class))).thenReturn(Optional.of(usuarioResponseDTO));

        mockMvc.perform(put("/api/auth/usuario/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuarioRegistroDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    void buscarPorUsername_deberiaRetornarLista() throws Exception {
        when(usuarioService.buscarPorUsername("test")).thenReturn(Arrays.asList(usuarioResponseDTO));

        mockMvc.perform(get("/api/auth/usuario/buscar?username=test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("testuser"));
    }

    @Test
    void validarToken_cuandoEsValido_deberiaRetornar200() throws Exception {
        when(jwtUtils.validarToken("valid-token")).thenReturn(true);
        when(jwtUtils.getNombreUsuarioDesdeToken("valid-token")).thenReturn("testuser");

        mockMvc.perform(get("/api/auth/validar?token=valid-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.valido").value(true));
    }

    @Test
    void validarToken_cuandoEsInvalido_deberiaRetornar401() throws Exception {
        when(jwtUtils.validarToken("invalid-token")).thenReturn(false);

        mockMvc.perform(get("/api/auth/validar?token=invalid-token"))
                .andExpect(status().isUnauthorized());
    }
}
