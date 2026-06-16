package cl.eventos.ms_soporte.controller;

import cl.eventos.ms_soporte.dto.TicketConDetalleDTO;
import cl.eventos.ms_soporte.dto.TicketRequestDTO;
import cl.eventos.ms_soporte.dto.TicketResponseDTO;
import cl.eventos.ms_soporte.service.TicketSoporteService;
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

@WebMvcTest(TicketSoporteController.class)
public class TicketSoporteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TicketSoporteService ticketSoporteService;

    @Autowired
    private ObjectMapper objectMapper;

    private TicketRequestDTO requestDTO;
    private TicketResponseDTO responseDTO;
    private TicketConDetalleDTO detalleDTO;

    @BeforeEach
    void setUp() {
        requestDTO = new TicketRequestDTO();
        requestDTO.setUsuarioId(100L);
        requestDTO.setOrdenId(50L);
        requestDTO.setAsunto("Ayuda");
        requestDTO.setDescripcionProblema("Problema");

        responseDTO = new TicketResponseDTO(
                1L, 100L, 50L, "Ayuda", "Problema", "ABIERTO", new Date()
        );

        detalleDTO = new TicketConDetalleDTO(
                1L, 100L, 50L, "Ayuda", "Problema", "ABIERTO", new Date(), null, null
        );
    }

    @Test
    public void testListar_ConDatos() throws Exception {
        when(ticketSoporteService.obtenerTodos()).thenReturn(Arrays.asList(responseDTO));

        mockMvc.perform(get("/api/soporte"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    public void testListar_SinDatos() throws Exception {
        when(ticketSoporteService.obtenerTodos()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/soporte"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testCrear() throws Exception {
        when(ticketSoporteService.guardar(any(TicketRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/soporte")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    public void testBuscar_CuandoExiste() throws Exception {
        when(ticketSoporteService.obtenerPorId(1L)).thenReturn(Optional.of(responseDTO));

        mockMvc.perform(get("/api/soporte/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    public void testBuscar_CuandoNoExiste() throws Exception {
        when(ticketSoporteService.obtenerPorId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/soporte/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testBuscarConDetalle_CuandoExiste() throws Exception {
        when(ticketSoporteService.obtenerDetalleCompleto(1L)).thenReturn(Optional.of(detalleDTO));

        mockMvc.perform(get("/api/soporte/1/detalle"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    public void testBuscarConDetalle_CuandoNoExiste() throws Exception {
        when(ticketSoporteService.obtenerDetalleCompleto(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/soporte/99/detalle"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testCambiarEstado_CuandoExiste() throws Exception {
        when(ticketSoporteService.actualizarEstado(1L, "CERRADO")).thenReturn(Optional.of(responseDTO));

        mockMvc.perform(patch("/api/soporte/1/estado")
                .param("nuevoEstado", "CERRADO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    public void testCambiarEstado_CuandoNoExiste() throws Exception {
        when(ticketSoporteService.actualizarEstado(99L, "CERRADO")).thenReturn(Optional.empty());

        mockMvc.perform(patch("/api/soporte/99/estado")
                .param("nuevoEstado", "CERRADO"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testBorrar_CuandoExiste() throws Exception {
        when(ticketSoporteService.obtenerPorId(1L)).thenReturn(Optional.of(responseDTO));
        doNothing().when(ticketSoporteService).eliminar(1L);

        mockMvc.perform(delete("/api/soporte/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testBorrar_CuandoNoExiste() throws Exception {
        when(ticketSoporteService.obtenerPorId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/soporte/99"))
                .andExpect(status().isNotFound());
    }
}
