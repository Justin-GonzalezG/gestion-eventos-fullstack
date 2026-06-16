package cl.eventos.ms_ordenes.service;

import cl.eventos.ms_ordenes.client.AuthClient;
import cl.eventos.ms_ordenes.client.PagoClient;
import cl.eventos.ms_ordenes.client.TicketClient;
import cl.eventos.ms_ordenes.client.UsuarioClient;
import cl.eventos.ms_ordenes.dto.DetalleRequestDTO;
import cl.eventos.ms_ordenes.dto.OrdenRequestDTO;
import cl.eventos.ms_ordenes.dto.TicketDTO;
import cl.eventos.ms_ordenes.dto.UsuarioDTO;
import cl.eventos.ms_ordenes.model.DetalleOrden;
import cl.eventos.ms_ordenes.model.Orden;
import cl.eventos.ms_ordenes.repository.OrdenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
public class OrdenServiceTest {

    @Autowired
    private OrdenService ordenService;

    @MockBean
    private OrdenRepository ordenRepository;

    @MockBean
    private TicketClient ticketClient;

    @MockBean
    private AuthClient authClient;

    @MockBean
    private UsuarioClient usuarioClient;

    @MockBean
    private PagoClient pagoClient;

    private OrdenRequestDTO requestDTO;
    private Orden orden;
    private UsuarioDTO usuarioDTO;
    private TicketDTO ticketDTO;
    private Map<String, Object> tokenValido;

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

        DetalleOrden detalleOrden = new DetalleOrden();
        detalleOrden.setId(100L);
        detalleOrden.setTicketId(1L);
        detalleOrden.setCantidad(2);
        detalleOrden.setPrecioUnitario(new BigDecimal("50000"));
        detalleOrden.setSubtotal(new BigDecimal("100000"));
        detalleOrden.setOrden(orden);

        orden.setDetalles(Arrays.asList(detalleOrden));

        usuarioDTO = new UsuarioDTO();
        usuarioDTO.setId(1L);
        usuarioDTO.setNombre("Juan");

        ticketDTO = new TicketDTO();
        ticketDTO.setId(1L);
        ticketDTO.setPrecio(new BigDecimal("50000"));
        ticketDTO.setStock(10);

        tokenValido = new HashMap<>();
        tokenValido.put("valido", true);
    }

    @Test
    public void testCrearOrden_Exito() {
        when(authClient.validarToken("valid-token")).thenReturn(tokenValido);
        when(usuarioClient.obtenerUsuarioPorId(1L)).thenReturn(usuarioDTO);
        when(ticketClient.obtenerTicketPorId(1L)).thenReturn(ticketDTO);
        when(ordenRepository.save(any(Orden.class))).thenReturn(orden);
        doNothing().when(ticketClient).actualizarStock(anyLong(), anyInt());

        Orden result = ordenService.crearOrden(requestDTO, "valid-token");

        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals("PENDIENTE", result.getEstado());
        verify(ordenRepository, times(1)).save(any(Orden.class));
        verify(ticketClient, times(1)).actualizarStock(1L, 8);
    }

    @Test
    public void testCrearOrden_TokenInvalido() {
        Map<String, Object> tokenInvalido = new HashMap<>();
        tokenInvalido.put("valido", false);

        when(authClient.validarToken("invalid-token")).thenReturn(tokenInvalido);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            ordenService.crearOrden(requestDTO, "invalid-token");
        });

        assertEquals("Acceso denegado: Token no válido.", exception.getMessage());
    }

    @Test
    public void testCrearOrden_TokenNulo() {
        when(authClient.validarToken("invalid-token")).thenReturn(null);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            ordenService.crearOrden(requestDTO, "invalid-token");
        });

        assertEquals("Acceso denegado: Token no válido.", exception.getMessage());
    }

    @Test
    public void testCrearOrden_UsuarioNoExiste() {
        when(authClient.validarToken("valid-token")).thenReturn(tokenValido);
        when(usuarioClient.obtenerUsuarioPorId(1L)).thenReturn(null);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            ordenService.crearOrden(requestDTO, "valid-token");
        });

        assertEquals("El usuario con ID 1 no existe.", exception.getMessage());
    }

    @Test
    public void testCrearOrden_TicketNoExiste() {
        when(authClient.validarToken("valid-token")).thenReturn(tokenValido);
        when(usuarioClient.obtenerUsuarioPorId(1L)).thenReturn(usuarioDTO);
        when(ticketClient.obtenerTicketPorId(1L)).thenReturn(null);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            ordenService.crearOrden(requestDTO, "valid-token");
        });

        assertEquals("El ticket ID 1 no existe.", exception.getMessage());
    }

    @Test
    public void testCrearOrden_StockInsuficiente() {
        ticketDTO.setStock(1); // Menos de lo solicitado (2)
        when(authClient.validarToken("valid-token")).thenReturn(tokenValido);
        when(usuarioClient.obtenerUsuarioPorId(1L)).thenReturn(usuarioDTO);
        when(ticketClient.obtenerTicketPorId(1L)).thenReturn(ticketDTO);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            ordenService.crearOrden(requestDTO, "valid-token");
        });

        assertEquals("Stock insuficiente para el ticket: 1", exception.getMessage());
    }

    @Test
    public void testListarTodas() {
        when(ordenRepository.findAll()).thenReturn(Arrays.asList(orden));

        List<Orden> result = ordenService.listarTodas();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("PENDIENTE", result.get(0).getEstado());
    }

    @Test
    public void testObtenerPorId_CuandoExiste() {
        when(ordenRepository.findById(10L)).thenReturn(Optional.of(orden));

        Optional<Orden> result = ordenService.obtenerPorId(10L);

        assertTrue(result.isPresent());
        assertEquals("PENDIENTE", result.get().getEstado());
    }

    @Test
    public void testObtenerPorId_CuandoNoExiste() {
        when(ordenRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Orden> result = ordenService.obtenerPorId(99L);

        assertFalse(result.isPresent());
    }

    @Test
    public void testObtenerPorUsuario() {
        when(ordenRepository.findByUsuarioId(1L)).thenReturn(Arrays.asList(orden));

        List<Orden> result = ordenService.obtenerPorUsuario(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getUsuarioId());
    }

    @Test
    public void testActualizarEstado_CuandoExiste() {
        when(ordenRepository.findById(10L)).thenReturn(Optional.of(orden));
        when(ordenRepository.save(any(Orden.class))).thenReturn(orden);

        Optional<Orden> result = ordenService.actualizarEstado(10L, "PAGADA");

        assertTrue(result.isPresent());
        assertEquals("PAGADA", result.get().getEstado());
    }

    @Test
    public void testActualizarEstado_CuandoNoExiste() {
        when(ordenRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Orden> result = ordenService.actualizarEstado(99L, "PAGADA");

        assertFalse(result.isPresent());
    }

    @Test
    public void testEliminarOrden_CuandoExiste() {
        when(ordenRepository.existsById(10L)).thenReturn(true);
        doNothing().when(ordenRepository).deleteById(10L);

        ordenService.eliminarOrden(10L);

        verify(ordenRepository, times(1)).deleteById(10L);
    }

    @Test
    public void testEliminarOrden_CuandoNoExiste() {
        when(ordenRepository.existsById(99L)).thenReturn(false);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            ordenService.eliminarOrden(99L);
        });

        assertEquals("La orden con ID 99 no existe.", exception.getMessage());
    }

    @Test
    public void testEstaPagado() {
        when(ordenRepository.existsByEstadoAndDetalles_TicketId("PAGADA", 1L)).thenReturn(true);

        boolean result = ordenService.estaPagado(1L);

        assertTrue(result);
    }
}
