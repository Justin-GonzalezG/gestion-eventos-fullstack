package cl.eventos.ms_recomendacion.controller;

import cl.eventos.ms_recomendacion.dto.RecomendacionConDetalleDTO;
import cl.eventos.ms_recomendacion.dto.RecomendacionRequestDTO;
import cl.eventos.ms_recomendacion.dto.RecomendacionResponseDTO;
import cl.eventos.ms_recomendacion.service.RecomendacionService;
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

@WebMvcTest(RecomendacionController.class)
public class RecomendacionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RecomendacionService recomendacionService;

    @Autowired
    private ObjectMapper objectMapper;

    private RecomendacionResponseDTO responseDTO;
    private RecomendacionRequestDTO requestDTO;
    private RecomendacionConDetalleDTO detalleDTO;

    @BeforeEach
    void setUp() {
        responseDTO = new RecomendacionResponseDTO(1L, 10L, 100L, "Porque te gusta el rock", 95, new Date());

        requestDTO = new RecomendacionRequestDTO();
        requestDTO.setUsuarioId(10L);
        requestDTO.setEventoSugeridoId(100L);
        requestDTO.setMotivo("Porque te gusta el rock");
        requestDTO.setNivelAfinidad(95);

        detalleDTO = new RecomendacionConDetalleDTO(1L, 10L, "Porque te gusta el rock", 95, new Date().toString(), null, null);
    }

    @Test
    public void testListar() throws Exception {
        when(recomendacionService.obtenerTodas()).thenReturn(Arrays.asList(responseDTO));

        mockMvc.perform(get("/api/recomendaciones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].motivo").value("Porque te gusta el rock"));
    }

    @Test
    public void testListar_Vacio() throws Exception {
        when(recomendacionService.obtenerTodas()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/recomendaciones"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testCrear() throws Exception {
        when(recomendacionService.guardar(any(RecomendacionRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/recomendaciones")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.motivo").value("Porque te gusta el rock"));
    }

    @Test
    public void testBuscar_CuandoExiste() throws Exception {
        when(recomendacionService.obtenerPorId(1L)).thenReturn(Optional.of(responseDTO));

        mockMvc.perform(get("/api/recomendaciones/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.motivo").value("Porque te gusta el rock"));
    }

    @Test
    public void testBuscar_CuandoNoExiste() throws Exception {
        when(recomendacionService.obtenerPorId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/recomendaciones/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testObtenerDetalle_CuandoExiste() throws Exception {
        when(recomendacionService.obtenerDetalleCompleto(1L)).thenReturn(Optional.of(detalleDTO));

        mockMvc.perform(get("/api/recomendaciones/1/detalle"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.motivo").value("Porque te gusta el rock"));
    }

    @Test
    public void testObtenerDetalle_CuandoNoExiste() throws Exception {
        when(recomendacionService.obtenerDetalleCompleto(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/recomendaciones/99/detalle"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testActualizar_CuandoExiste() throws Exception {
        when(recomendacionService.actualizar(eq(1L), any(RecomendacionRequestDTO.class))).thenReturn(Optional.of(responseDTO));

        mockMvc.perform(put("/api/recomendaciones/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.motivo").value("Porque te gusta el rock"));
    }

    @Test
    public void testActualizar_CuandoNoExiste() throws Exception {
        when(recomendacionService.actualizar(eq(99L), any(RecomendacionRequestDTO.class))).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/recomendaciones/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testBorrar_CuandoExiste() throws Exception {
        when(recomendacionService.obtenerPorId(1L)).thenReturn(Optional.of(responseDTO));
        doNothing().when(recomendacionService).eliminar(1L);

        mockMvc.perform(delete("/api/recomendaciones/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testBorrar_CuandoNoExiste() throws Exception {
        when(recomendacionService.obtenerPorId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/recomendaciones/99"))
                .andExpect(status().isNotFound());
    }
}
