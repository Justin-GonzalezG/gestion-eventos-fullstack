package cl.eventos.ms_ordenes.controller;

import cl.eventos.ms_ordenes.dto.DetalleRequestDTO;
import cl.eventos.ms_ordenes.dto.OrdenRequestDTO;
import cl.eventos.ms_ordenes.model.Orden;
import cl.eventos.ms_ordenes.service.OrdenService;
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
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrdenController.class)
public class OrdenControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrdenService ordenService;

    @Autowired
    private ObjectMapper objectMapper;

    private OrdenRequestDTO requestDTO;
    private Orden orden;

    @BeforeEach
    void setUp() {
        DetalleRequestDTO detalleRequest = new DetalleRequestDTO();
        detalleRequest.setTicketId(1L);
        detalleRequest.setCantidad(2);

        requestDTO = new OrdenRequestDTO();
        requestDTO.setUsuarioId(1L);
        requestDTO.setDetalles(Arrays.asList(detalleRequest));

        orden = new Orden();
        orden.setId(10L);
        orden.setUsuarioId(1L);
        orden.setEstado("PENDIENTE");
        orden.setGranTotal(new BigDecimal("100000"));
    }

    @Test
    public void testCrear() throws Exception {
        when(ordenService.crearOrden(any(OrdenRequestDTO.class), eq("valid-token"))).thenReturn(orden);

        mockMvc.perform(post("/api/ordenes/crear")
                .header("Authorization", "Bearer valid-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string("La Orden ha sido guardada exitosamente."));
    }

    @Test
    public void testListar() throws Exception {
        when(ordenService.listarTodas()).thenReturn(Arrays.asList(orden));

        mockMvc.perform(get("/api/ordenes/listar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10L))
                .andExpect(jsonPath("$[0].estado").value("PENDIENTE"));
    }

    @Test
    public void testObtenerPorId_CuandoExiste() throws Exception {
        when(ordenService.obtenerPorId(10L)).thenReturn(Optional.of(orden));

        mockMvc.perform(get("/api/ordenes/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10L));
    }

    @Test
    public void testObtenerPorId_CuandoNoExiste() throws Exception {
        when(ordenService.obtenerPorId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/ordenes/99"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Orden no encontrada"));
    }

    @Test
    public void testFiltrar() throws Exception {
        when(ordenService.obtenerPorUsuario(1L)).thenReturn(Arrays.asList(orden));

        mockMvc.perform(get("/api/ordenes/filtrar/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10L));
    }

    @Test
    public void testActualizar_EstadoAprobado() throws Exception {
        orden.setEstado("PAGADA");
        when(ordenService.actualizarEstado(10L, "PAGADA")).thenReturn(Optional.of(orden));

        mockMvc.perform(put("/api/ordenes/actualizar/10").param("nuevoEstado", "PAGADA"))
                .andExpect(status().isOk())
                .andExpect(content().string("El estado de la orden #10 ahora es: PAGADA"));
    }

    @Test
    public void testActualizar_EstadoRechazado() throws Exception {
        orden.setEstado("RECHAZADO");
        when(ordenService.actualizarEstado(10L, "RECHAZADO")).thenReturn(Optional.of(orden));

        mockMvc.perform(put("/api/ordenes/actualizar/10").param("nuevoEstado", "RECHAZADO"))
                .andExpect(status().isOk())
                .andExpect(content().string("Su pago ha sido cambiado a RECHAZADO."));
    }

    @Test
    public void testActualizar_CuandoNoExiste() throws Exception {
        when(ordenService.actualizarEstado(99L, "PAGADA")).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/ordenes/actualizar/99").param("nuevoEstado", "PAGADA"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Orden no encontrada para actualizar"));
    }

    @Test
    public void testEliminar_CuandoExiste() throws Exception {
        when(ordenService.obtenerPorId(10L)).thenReturn(Optional.of(orden));
        doNothing().when(ordenService).eliminarOrden(10L);

        mockMvc.perform(delete("/api/ordenes/eliminar/10"))
                .andExpect(status().isOk())
                .andExpect(content().string("La orden #10 ha sido eliminada correctamente."));
    }

    @Test
    public void testEliminar_CuandoNoExiste() throws Exception {
        when(ordenService.obtenerPorId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/ordenes/eliminar/99"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("La orden con ID 99 no existe."));
    }

    @Test
    public void testVerificarPagoTicket() throws Exception {
        when(ordenService.estaPagado(1L)).thenReturn(true);

        mockMvc.perform(get("/api/ordenes/validar-pago/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }
}
