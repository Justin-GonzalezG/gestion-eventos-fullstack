package cl.eventos.ms_checkin.controller;

import cl.eventos.ms_checkin.dto.CheckInRequestDTO;
import cl.eventos.ms_checkin.dto.CheckInResponseDTO;
import cl.eventos.ms_checkin.service.CheckInService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CheckInController.class)
public class CheckInControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CheckInService checkInService;

    @Autowired
    private ObjectMapper objectMapper;

    private CheckInRequestDTO requestDTO;
    private CheckInResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        requestDTO = new CheckInRequestDTO();
        requestDTO.setTicketId(10L);

        responseDTO = new CheckInResponseDTO(
                1L, 10L, LocalDateTime.now()
        );
    }

    @Test
    public void testRegistrar() throws Exception {
        when(checkInService.registrarIngreso(any(CheckInRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/checkin/registrar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    public void testListar_ConDatos() throws Exception {
        when(checkInService.obtenerTodos()).thenReturn(Arrays.asList(responseDTO));

        mockMvc.perform(get("/api/checkin/listar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    public void testListar_SinDatos() throws Exception {
        when(checkInService.obtenerTodos()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/checkin/listar"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testBuscarPorTicket_CuandoExiste() throws Exception {
        when(checkInService.buscarPorTicket(10L)).thenReturn(Optional.of(responseDTO));

        mockMvc.perform(get("/api/checkin/ticket/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    public void testBuscarPorTicket_CuandoNoExiste() throws Exception {
        when(checkInService.buscarPorTicket(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/checkin/ticket/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testActualizar_CuandoExiste() throws Exception {
        when(checkInService.actualizar(eq(1L), any(CheckInRequestDTO.class))).thenReturn(Optional.of(responseDTO));

        mockMvc.perform(put("/api/checkin/actualizar/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    public void testActualizar_CuandoNoExiste() throws Exception {
        when(checkInService.actualizar(eq(99L), any(CheckInRequestDTO.class))).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/checkin/actualizar/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testEliminar_CuandoExiste() throws Exception {
        when(checkInService.obtenerTodos()).thenReturn(Arrays.asList(responseDTO));
        doNothing().when(checkInService).eliminar(1L);

        mockMvc.perform(delete("/api/checkin/eliminar/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testEliminar_CuandoNoExiste() throws Exception {
        when(checkInService.obtenerTodos()).thenReturn(Collections.emptyList());

        mockMvc.perform(delete("/api/checkin/eliminar/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testObtenerAforo() throws Exception {
        when(checkInService.obtenerTotalAsistentes()).thenReturn(5L);

        mockMvc.perform(get("/api/checkin/aforo"))
                .andExpect(status().isOk())
                .andExpect(content().string("5"));
    }
}
