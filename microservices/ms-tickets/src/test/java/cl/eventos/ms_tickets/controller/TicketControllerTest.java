package cl.eventos.ms_tickets.controller;

import cl.eventos.ms_tickets.dto.TicketRequestDTO;
import cl.eventos.ms_tickets.dto.TicketResponseDTO;
import cl.eventos.ms_tickets.service.TicketService;
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

@WebMvcTest(TicketController.class)
public class TicketControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TicketService ticketService;

    @Autowired
    private ObjectMapper objectMapper;

    private TicketResponseDTO responseDTO;
    private TicketRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        responseDTO = new TicketResponseDTO(10L, "VIP", new BigDecimal("50000"), 100, "Conciertos");

        requestDTO = new TicketRequestDTO();
        requestDTO.setTipo("VIP");
        requestDTO.setPrecio(new BigDecimal("50000"));
        requestDTO.setStock(100);
        requestDTO.setCategoriaId(1L);
    }

    @Test
    public void testObtenerTodos() throws Exception {
        when(ticketService.obtenerTodos()).thenReturn(Arrays.asList(responseDTO));

        mockMvc.perform(get("/api/tickets/listar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tipo").value("VIP"));
    }

    @Test
    public void testObtenerPorId_CuandoExiste() throws Exception {
        when(ticketService.obtenerPorId(10L)).thenReturn(Optional.of(responseDTO));

        mockMvc.perform(get("/api/tickets/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipo").value("VIP"));
    }

    @Test
    public void testObtenerPorId_CuandoNoExiste() throws Exception {
        when(ticketService.obtenerPorId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/tickets/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testCrear() throws Exception {
        when(ticketService.guardar(any(TicketRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/tickets/crear")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.mensaje").value("El ticket fue creado con éxito"))
                .andExpect(jsonPath("$.ticket.tipo").value("VIP"));
    }

    @Test
    public void testActualizar_CuandoExiste() throws Exception {
        when(ticketService.actualizar(eq(10L), any(TicketRequestDTO.class))).thenReturn(Optional.of(responseDTO));

        mockMvc.perform(put("/api/tickets/10")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("El ticket fue actualizado con éxito"))
                .andExpect(jsonPath("$.ticket.tipo").value("VIP"));
    }

    @Test
    public void testActualizar_CuandoNoExiste() throws Exception {
        when(ticketService.actualizar(eq(99L), any(TicketRequestDTO.class))).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/tickets/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testEliminar_CuandoExiste() throws Exception {
        when(ticketService.obtenerPorId(10L)).thenReturn(Optional.of(responseDTO));
        doNothing().when(ticketService).eliminar(10L);

        mockMvc.perform(delete("/api/tickets/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Ticket eliminado con éxito de la base de datos."));
    }

    @Test
    public void testEliminar_CuandoNoExiste() throws Exception {
        when(ticketService.obtenerPorId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/tickets/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("El ticket con ID 99 no existe."));
    }

    @Test
    public void testBuscarPorTipo() throws Exception {
        when(ticketService.buscarPorTipo("vi")).thenReturn(Arrays.asList(responseDTO));

        mockMvc.perform(get("/api/tickets/buscar").param("tipo", "vi"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tipo").value("VIP"));
    }

    @Test
    public void testBuscarPorCategoria() throws Exception {
        when(ticketService.buscarPorCategoria(1L)).thenReturn(Arrays.asList(responseDTO));

        mockMvc.perform(get("/api/tickets/categoria/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tipo").value("VIP"));
    }

    @Test
    public void testBajoPresupuesto() throws Exception {
        when(ticketService.buscarBajoPresupuesto(any(BigDecimal.class))).thenReturn(Arrays.asList(responseDTO));

        mockMvc.perform(get("/api/tickets/presupuesto").param("max", "60000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tipo").value("VIP"));
    }

    @Test
    public void testActualizarStock() throws Exception {
        doNothing().when(ticketService).actualizarSoloStock(10L, 50);

        mockMvc.perform(put("/api/tickets/10/stock").param("nuevoStock", "50"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Stock actualizado a: 50"));
    }
}
