package cl.eventos.ms_soporte.service;

import cl.eventos.ms_soporte.client.OrdenClient;
import cl.eventos.ms_soporte.client.UsuarioClient;
import cl.eventos.ms_soporte.dto.OrdenDTO;
import cl.eventos.ms_soporte.dto.TicketConDetalleDTO;
import cl.eventos.ms_soporte.dto.TicketRequestDTO;
import cl.eventos.ms_soporte.dto.TicketResponseDTO;
import cl.eventos.ms_soporte.dto.UsuarioDTO;
import cl.eventos.ms_soporte.model.TicketSoporte;
import cl.eventos.ms_soporte.repository.TicketSoporteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
public class TicketSoporteServiceTest {

    @Autowired
    private TicketSoporteService ticketSoporteService;

    @MockBean
    private TicketSoporteRepository ticketSoporteRepository;

    @MockBean
    private UsuarioClient usuarioClient;

    @MockBean
    private OrdenClient ordenClient;

    private TicketSoporte ticket;
    private TicketRequestDTO requestDTO;
    private UsuarioDTO usuarioDTO;
    private OrdenDTO ordenDTO;

    @BeforeEach
    void setUp() {
        ticket = new TicketSoporte();
        ticket.setId(1L);
        ticket.setUsuarioId(100L);
        ticket.setOrdenId(50L);
        ticket.setAsunto("Reclamo");
        ticket.setDescripcionProblema("No llegó mi correo");
        ticket.setEstado("ABIERTO");
        ticket.setFechaCreacion(new Date());

        requestDTO = new TicketRequestDTO();
        requestDTO.setUsuarioId(100L);
        requestDTO.setOrdenId(50L);
        requestDTO.setAsunto("Reclamo");
        requestDTO.setDescripcionProblema("No llegó mi correo");

        usuarioDTO = new UsuarioDTO();
        usuarioDTO.setRun("11111111-1");
        usuarioDTO.setNombre("Benjamín");

        ordenDTO = new OrdenDTO();
        ordenDTO.setId(50L);
        ordenDTO.setGranTotal(new java.math.BigDecimal("1500"));
    }

    @Test
    public void testObtenerTodos() {
        when(ticketSoporteRepository.findAll()).thenReturn(Arrays.asList(ticket));

        List<TicketResponseDTO> result = ticketSoporteService.obtenerTodos();

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }

    @Test
    public void testObtenerPorId_CuandoExiste() {
        when(ticketSoporteRepository.findById(1L)).thenReturn(Optional.of(ticket));

        Optional<TicketResponseDTO> result = ticketSoporteService.obtenerPorId(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
    }

    @Test
    public void testObtenerPorId_CuandoNoExiste() {
        when(ticketSoporteRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<TicketResponseDTO> result = ticketSoporteService.obtenerPorId(99L);

        assertFalse(result.isPresent());
    }

    @Test
    public void testGuardar() {
        when(ticketSoporteRepository.save(any(TicketSoporte.class))).thenReturn(ticket);

        TicketResponseDTO result = ticketSoporteService.guardar(requestDTO);

        assertNotNull(result);
        assertEquals("ABIERTO", result.getEstado());
        verify(ticketSoporteRepository, times(1)).save(any(TicketSoporte.class));
    }

    @Test
    public void testActualizarEstado_CuandoExiste() {
        when(ticketSoporteRepository.findById(1L)).thenReturn(Optional.of(ticket));
        when(ticketSoporteRepository.save(any(TicketSoporte.class))).thenReturn(ticket);

        Optional<TicketResponseDTO> result = ticketSoporteService.actualizarEstado(1L, "CERRADO");

        assertTrue(result.isPresent());
        assertEquals("CERRADO", result.get().getEstado()); // Modificado en el mock por referencia
    }

    @Test
    public void testActualizarEstado_CuandoNoExiste() {
        when(ticketSoporteRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<TicketResponseDTO> result = ticketSoporteService.actualizarEstado(99L, "CERRADO");

        assertFalse(result.isPresent());
    }

    @Test
    public void testEliminar() {
        doNothing().when(ticketSoporteRepository).deleteById(1L);

        ticketSoporteService.eliminar(1L);

        verify(ticketSoporteRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testObtenerDetalleCompleto_ExitoConOrden() {
        when(ticketSoporteRepository.findById(1L)).thenReturn(Optional.of(ticket));
        when(usuarioClient.obtenerUsuarioPorId(100L)).thenReturn(ResponseEntity.ok(usuarioDTO));
        when(ordenClient.obtenerOrdenPorId(50L)).thenReturn(ordenDTO);

        Optional<TicketConDetalleDTO> result = ticketSoporteService.obtenerDetalleCompleto(1L);

        assertTrue(result.isPresent());
        assertNotNull(result.get().getDetalleUsuario());
        assertEquals("Benjamín", result.get().getDetalleUsuario().getNombre());
        assertNotNull(result.get().getDetalleOrden());
        assertEquals(50L, result.get().getDetalleOrden().getId());
    }

    @Test
    public void testObtenerDetalleCompleto_ExitoSinOrden() {
        ticket.setOrdenId(null);
        when(ticketSoporteRepository.findById(1L)).thenReturn(Optional.of(ticket));
        when(usuarioClient.obtenerUsuarioPorId(100L)).thenReturn(ResponseEntity.ok(usuarioDTO));

        Optional<TicketConDetalleDTO> result = ticketSoporteService.obtenerDetalleCompleto(1L);

        assertTrue(result.isPresent());
        assertNotNull(result.get().getDetalleUsuario());
        assertNull(result.get().getDetalleOrden());
        verify(ordenClient, never()).obtenerOrdenPorId(anyLong());
    }

    @Test
    public void testObtenerDetalleCompleto_FallaUsuarioClient() {
        when(ticketSoporteRepository.findById(1L)).thenReturn(Optional.of(ticket));
        when(usuarioClient.obtenerUsuarioPorId(100L)).thenThrow(new RuntimeException("Error Usuario"));
        when(ordenClient.obtenerOrdenPorId(50L)).thenReturn(ordenDTO);

        Optional<TicketConDetalleDTO> result = ticketSoporteService.obtenerDetalleCompleto(1L);

        assertTrue(result.isPresent());
        assertNull(result.get().getDetalleUsuario());
        assertNotNull(result.get().getDetalleOrden());
    }

    @Test
    public void testObtenerDetalleCompleto_FallaOrdenClient() {
        when(ticketSoporteRepository.findById(1L)).thenReturn(Optional.of(ticket));
        when(usuarioClient.obtenerUsuarioPorId(100L)).thenReturn(ResponseEntity.ok(usuarioDTO));
        when(ordenClient.obtenerOrdenPorId(50L)).thenThrow(new RuntimeException("Error Orden"));

        Optional<TicketConDetalleDTO> result = ticketSoporteService.obtenerDetalleCompleto(1L);

        assertTrue(result.isPresent());
        assertNotNull(result.get().getDetalleUsuario());
        assertNull(result.get().getDetalleOrden());
    }

    @Test
    public void testObtenerDetalleCompleto_CuandoNoExiste() {
        when(ticketSoporteRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<TicketConDetalleDTO> result = ticketSoporteService.obtenerDetalleCompleto(99L);

        assertFalse(result.isPresent());
    }
}
