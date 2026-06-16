package cl.eventos.ms_pagos.controller;

import cl.eventos.ms_pagos.dto.PagoRequestDTO;
import cl.eventos.ms_pagos.dto.PagoResponseDTO;
import cl.eventos.ms_pagos.service.PagoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PagoController.class)
public class PagoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PagoService pagoService;

    @Autowired
    private ObjectMapper objectMapper;

    private PagoRequestDTO requestDTO;
    private PagoResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        requestDTO = new PagoRequestDTO();
        requestDTO.setOrdenId(10L);
        requestDTO.setMonto(new BigDecimal("100000"));
        requestDTO.setMetodoPago("TARJETA");

        responseDTO = new PagoResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setOrdenId(10L);
        responseDTO.setMonto(new BigDecimal("100000"));
        responseDTO.setMetodoPago("TARJETA");
        responseDTO.setEstadoPago("APROBADO");
        responseDTO.setFechaPago(LocalDateTime.now());
    }

    @Test
    public void testCrear() throws Exception {
        when(pagoService.save(any(PagoRequestDTO.class), eq("valid-token"))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/pagos/crear")
                .header("Authorization", "Bearer valid-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.mensaje").value("Pago procesado exitosamente"))
                .andExpect(jsonPath("$.pago.id").value(1L));
    }

    @Test
    public void testListar() throws Exception {
        when(pagoService.findAll()).thenReturn(Arrays.asList(responseDTO));

        mockMvc.perform(get("/api/pagos/listar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].estadoPago").value("APROBADO"));
    }

    @Test
    public void testBuscarPorId_CuandoExiste() throws Exception {
        when(pagoService.findByIdOptional(1L)).thenReturn(Optional.of(responseDTO));

        mockMvc.perform(get("/api/pagos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    public void testBuscarPorId_CuandoNoExiste() throws Exception {
        when(pagoService.findByIdOptional(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/pagos/99"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Pago no encontrado con el ID: 99"));
    }

    @Test
    public void testActualizar_CuandoExiste() throws Exception {
        when(pagoService.updateOptional(eq(1L), any(PagoRequestDTO.class))).thenReturn(Optional.of(responseDTO));

        mockMvc.perform(put("/api/pagos/actualizar/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    public void testActualizar_CuandoNoExiste() throws Exception {
        when(pagoService.updateOptional(eq(99L), any(PagoRequestDTO.class))).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/pagos/actualizar/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("No se pudo actualizar porque el pago no existe."));
    }

    @Test
    public void testActualizarEstado_CuandoExiste() throws Exception {
        when(pagoService.actualizarEstadoOptional(1L, "RECHAZADO")).thenReturn(Optional.of(responseDTO));

        mockMvc.perform(patch("/api/pagos/actualizar-estado/1").param("nuevoEstado", "RECHAZADO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Estado de pago actualizado correctamente"))
                .andExpect(jsonPath("$.pago.id").value(1L));
    }

    @Test
    public void testActualizarEstado_CuandoNoExiste() throws Exception {
        when(pagoService.actualizarEstadoOptional(99L, "RECHAZADO")).thenReturn(Optional.empty());

        mockMvc.perform(patch("/api/pagos/actualizar-estado/99").param("nuevoEstado", "RECHAZADO"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("No se encontró el registro de pago para cambiar su estado."));
    }

    @Test
    public void testEliminar_CuandoExiste() throws Exception {
        when(pagoService.existsById(1L)).thenReturn(true);
        doNothing().when(pagoService).delete(1L);

        mockMvc.perform(delete("/api/pagos/eliminar/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("El registro de pago ha sido eliminado correctamente de la base de datos."));
    }

    @Test
    public void testEliminar_CuandoNoExiste() throws Exception {
        when(pagoService.existsById(99L)).thenReturn(false);

        mockMvc.perform(delete("/api/pagos/eliminar/99"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("El registro de pago no existe en la base de datos."));
    }
}
