package cl.eventos.ms_reportes.controller;

import cl.eventos.ms_reportes.dto.ReporteRequestDTO;
import cl.eventos.ms_reportes.dto.ReporteResponseDTO;
import cl.eventos.ms_reportes.service.ReporteService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReporteController.class)
public class ReporteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReporteService reporteService;

    @Autowired
    private ObjectMapper objectMapper;

    private ReporteRequestDTO requestDTO;
    private ReporteResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        requestDTO = new ReporteRequestDTO();
        requestDTO.setPeriodo("Junio 2026");

        responseDTO = new ReporteResponseDTO(
                1L,
                null,
                "Junio 2026",
                new BigDecimal("1000"),
                5,
                "VIP"
        );
    }

    @Test
    public void testListar_ConDatos() throws Exception {
        when(reporteService.obtenerTodos()).thenReturn(Arrays.asList(responseDTO));

        mockMvc.perform(get("/api/reportes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    public void testListar_SinDatos() throws Exception {
        when(reporteService.obtenerTodos()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/reportes"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testCrear() throws Exception {
        when(reporteService.guardar(any(ReporteRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/reportes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    public void testBuscar_CuandoExiste() throws Exception {
        when(reporteService.obtenerPorId(1L)).thenReturn(Optional.of(responseDTO));

        mockMvc.perform(get("/api/reportes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    public void testBuscar_CuandoNoExiste() throws Exception {
        when(reporteService.obtenerPorId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/reportes/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testActualizar_CuandoExiste() throws Exception {
        when(reporteService.actualizar(eq(1L), any(ReporteRequestDTO.class))).thenReturn(Optional.of(responseDTO));

        mockMvc.perform(put("/api/reportes/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    public void testActualizar_CuandoNoExiste() throws Exception {
        when(reporteService.actualizar(eq(99L), any(ReporteRequestDTO.class))).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/reportes/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testBorrar_CuandoExiste() throws Exception {
        when(reporteService.obtenerPorId(1L)).thenReturn(Optional.of(responseDTO));
        doNothing().when(reporteService).eliminar(1L);

        mockMvc.perform(delete("/api/reportes/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testBorrar_CuandoNoExiste() throws Exception {
        when(reporteService.obtenerPorId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/reportes/99"))
                .andExpect(status().isNotFound());
    }
}
