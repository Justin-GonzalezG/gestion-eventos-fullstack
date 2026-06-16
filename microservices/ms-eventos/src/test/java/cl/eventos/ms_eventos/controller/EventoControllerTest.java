package cl.eventos.ms_eventos.controller;

import cl.eventos.ms_eventos.dto.EventoRequestDTO;
import cl.eventos.ms_eventos.dto.EventoResponseDTO;
import cl.eventos.ms_eventos.service.EventoService;
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
import java.util.Date;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EventoController.class)
public class EventoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EventoService eventoService;

    @Autowired
    private ObjectMapper objectMapper;

    private EventoResponseDTO responseDTO;
    private EventoRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        responseDTO = new EventoResponseDTO(1L, "Concierto Rock", "Un gran concierto", new Date(), "Estadio", 500, "ACTIVO", null);

        requestDTO = new EventoRequestDTO();
        requestDTO.setNombre("Concierto Rock");
        requestDTO.setInformacionGeneral("Un gran concierto");
        requestDTO.setFechaHora(new Date());
        requestDTO.setUbicacion("Estadio");
        requestDTO.setCapacidadMaxima(500);
        requestDTO.setEstado("ACTIVO");
    }

    @Test
    public void testListar() throws Exception {
        when(eventoService.obtenerTodos()).thenReturn(Arrays.asList(responseDTO));

        mockMvc.perform(get("/api/eventos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Concierto Rock"));
    }

    @Test
    public void testListar_Vacio() throws Exception {
        when(eventoService.obtenerTodos()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/eventos"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testCrear() throws Exception {
        when(eventoService.guardar(any(EventoRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/eventos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Concierto Rock"));
    }

    @Test
    public void testBuscar_CuandoExiste() throws Exception {
        when(eventoService.obtenerPorId(1L)).thenReturn(Optional.of(responseDTO));

        mockMvc.perform(get("/api/eventos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Concierto Rock"));
    }

    @Test
    public void testBuscar_CuandoNoExiste() throws Exception {
        when(eventoService.obtenerPorId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/eventos/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testActualizar_CuandoExiste() throws Exception {
        when(eventoService.actualizar(eq(1L), any(EventoRequestDTO.class))).thenReturn(Optional.of(responseDTO));

        mockMvc.perform(put("/api/eventos/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Concierto Rock"));
    }

    @Test
    public void testActualizar_CuandoNoExiste() throws Exception {
        when(eventoService.actualizar(eq(99L), any(EventoRequestDTO.class))).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/eventos/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testBorrar_CuandoExiste() throws Exception {
        when(eventoService.obtenerPorId(1L)).thenReturn(Optional.of(responseDTO));
        doNothing().when(eventoService).eliminar(1L);

        mockMvc.perform(delete("/api/eventos/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testBorrar_CuandoNoExiste() throws Exception {
        when(eventoService.obtenerPorId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/eventos/99"))
                .andExpect(status().isNotFound());
    }
}
