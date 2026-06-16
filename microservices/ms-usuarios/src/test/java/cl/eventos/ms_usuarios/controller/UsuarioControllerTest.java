package cl.eventos.ms_usuarios.controller;

import cl.eventos.ms_usuarios.dto.UsuarioConAuthDTO;
import cl.eventos.ms_usuarios.dto.UsuarioRequestDTO;
import cl.eventos.ms_usuarios.dto.UsuarioResponseDTO;
import cl.eventos.ms_usuarios.service.UsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UsuarioController.class)
public class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioService usuarioService;

    @Autowired
    private ObjectMapper objectMapper;

    private UsuarioResponseDTO usuarioResponseDTO;
    private UsuarioRequestDTO usuarioRequestDTO;
    private UsuarioConAuthDTO usuarioConAuthDTO;

    @BeforeEach
    void setUp() {
        usuarioResponseDTO = new UsuarioResponseDTO(1L, "12345678-9", "Juan", "Perez", "juan@test.com", "987654321", "Calle Falsa 123");

        usuarioRequestDTO = new UsuarioRequestDTO();
        usuarioRequestDTO.setRun("12345678-9");
        usuarioRequestDTO.setNombre("Juan");
        usuarioRequestDTO.setApellido("Perez");
        usuarioRequestDTO.setEmail("juan@test.com");
        usuarioRequestDTO.setTelefono("987654321");
        usuarioRequestDTO.setDireccion("Calle Falsa 123");

        usuarioConAuthDTO = new UsuarioConAuthDTO();
        usuarioConAuthDTO.setId(1L);
        usuarioConAuthDTO.setRun("12345678-9");
        usuarioConAuthDTO.setNombre("Juan");
        usuarioConAuthDTO.setApellido("Perez");
        usuarioConAuthDTO.setEmail("juan@test.com");
        usuarioConAuthDTO.setTelefono("987654321");
        usuarioConAuthDTO.setDireccion("Calle Falsa 123");
        usuarioConAuthDTO.setUsername("juanito");
        usuarioConAuthDTO.setRol("CLIENTE");
    }

    @Test
    public void testListar() throws Exception {
        when(usuarioService.obtenerTodos()).thenReturn(Arrays.asList(usuarioResponseDTO));

        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Juan"));
    }

    @Test
    public void testListar_Vacio() throws Exception {
        when(usuarioService.obtenerTodos()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testCrear() throws Exception {
        when(usuarioService.guardar(any(UsuarioRequestDTO.class))).thenReturn(usuarioResponseDTO);

        mockMvc.perform(post("/api/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuarioRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Juan"));
    }

    @Test
    public void testBuscar_CuandoExiste() throws Exception {
        when(usuarioService.obtenerPorId(1L)).thenReturn(Optional.of(usuarioResponseDTO));

        mockMvc.perform(get("/api/usuarios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Juan"));
    }

    @Test
    public void testBuscar_CuandoNoExiste() throws Exception {
        when(usuarioService.obtenerPorId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/usuarios/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testBuscarConDetalle_CuandoExiste() throws Exception {
        when(usuarioService.obtenerUsuarioConAuth(1L)).thenReturn(Optional.of(usuarioConAuthDTO));

        mockMvc.perform(get("/api/usuarios/1/detalle"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Juan"))
                .andExpect(jsonPath("$.username").value("juanito"));
    }

    @Test
    public void testBuscarConDetalle_CuandoNoExiste() throws Exception {
        when(usuarioService.obtenerUsuarioConAuth(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/usuarios/99/detalle"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testActualizar_CuandoExiste() throws Exception {
        when(usuarioService.actualizar(eq(1L), any(UsuarioRequestDTO.class))).thenReturn(Optional.of(usuarioResponseDTO));

        mockMvc.perform(put("/api/usuarios/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuarioRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Juan"));
    }

    @Test
    public void testActualizar_CuandoNoExiste() throws Exception {
        when(usuarioService.actualizar(eq(99L), any(UsuarioRequestDTO.class))).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/usuarios/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuarioRequestDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testBorrar_CuandoExiste() throws Exception {
        when(usuarioService.obtenerPorId(1L)).thenReturn(Optional.of(usuarioResponseDTO));
        doNothing().when(usuarioService).eliminar(1L);

        mockMvc.perform(delete("/api/usuarios/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testBorrar_CuandoNoExiste() throws Exception {
        when(usuarioService.obtenerPorId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/usuarios/99"))
                .andExpect(status().isNotFound());
    }
}
