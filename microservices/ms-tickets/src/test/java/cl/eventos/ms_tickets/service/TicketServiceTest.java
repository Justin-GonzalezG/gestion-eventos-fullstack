package cl.eventos.ms_tickets.service;

import cl.eventos.ms_tickets.dto.TicketRequestDTO;
import cl.eventos.ms_tickets.dto.TicketResponseDTO;
import cl.eventos.ms_tickets.model.Categoria;
import cl.eventos.ms_tickets.model.Ticket;
import cl.eventos.ms_tickets.repository.CategoriaRepository;
import cl.eventos.ms_tickets.repository.TicketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
public class TicketServiceTest {

    @Autowired
    private TicketService ticketService;

    @MockBean
    private TicketRepository ticketRepository;

    @MockBean
    private CategoriaRepository categoriaRepository;

    private Ticket ticket;
    private Categoria categoria;
    private TicketRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNombre("Conciertos");
        categoria.setDescripcion("Eventos musicales");

        ticket = new Ticket();
        ticket.setId(10L);
        ticket.setTipo("VIP");
        ticket.setPrecio(new BigDecimal("50000"));
        ticket.setStock(100);
        ticket.setCategoria(categoria);

        requestDTO = new TicketRequestDTO();
        requestDTO.setTipo("VIP");
        requestDTO.setPrecio(new BigDecimal("50000"));
        requestDTO.setStock(100);
        requestDTO.setCategoriaId(1L);
    }

    @Test
    public void testObtenerTodos() {
        when(ticketRepository.findAll()).thenReturn(Arrays.asList(ticket));

        List<TicketResponseDTO> result = ticketService.obtenerTodos();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("VIP", result.get(0).getTipo());
        assertEquals("Conciertos", result.get(0).getCategoriaNombre());
    }

    @Test
    public void testObtenerPorId_CuandoExiste() {
        when(ticketRepository.findById(10L)).thenReturn(Optional.of(ticket));

        Optional<TicketResponseDTO> result = ticketService.obtenerPorId(10L);

        assertTrue(result.isPresent());
        assertEquals("VIP", result.get().getTipo());
    }

    @Test
    public void testObtenerPorId_SinCategoria() {
        Ticket ticketSinCategoria = new Ticket();
        ticketSinCategoria.setId(11L);
        ticketSinCategoria.setTipo("Normal");
        ticketSinCategoria.setCategoria(null);

        when(ticketRepository.findById(11L)).thenReturn(Optional.of(ticketSinCategoria));

        Optional<TicketResponseDTO> result = ticketService.obtenerPorId(11L);

        assertTrue(result.isPresent());
        assertEquals("Sin Categoría", result.get().getCategoriaNombre());
    }

    @Test
    public void testObtenerPorId_CuandoNoExiste() {
        when(ticketRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<TicketResponseDTO> result = ticketService.obtenerPorId(99L);

        assertFalse(result.isPresent());
    }

    @Test
    public void testGuardar_CategoriaExiste() {
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);

        TicketResponseDTO result = ticketService.guardar(requestDTO);

        assertNotNull(result);
        assertEquals("VIP", result.getTipo());
    }

    @Test
    public void testGuardar_CategoriaNoExiste() {
        when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

        requestDTO.setCategoriaId(99L);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            ticketService.guardar(requestDTO);
        });

        assertTrue(exception.getMessage().contains("Categoría NO encontrada"));
    }

    @Test
    public void testActualizar_TicketYCategoriaExisten() {
        when(ticketRepository.findById(10L)).thenReturn(Optional.of(ticket));
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);

        Optional<TicketResponseDTO> result = ticketService.actualizar(10L, requestDTO);

        assertTrue(result.isPresent());
        assertEquals("VIP", result.get().getTipo());
    }

    @Test
    public void testActualizar_TicketNoExiste() {
        when(ticketRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<TicketResponseDTO> result = ticketService.actualizar(99L, requestDTO);

        assertFalse(result.isPresent());
    }

    @Test
    public void testActualizar_CategoriaNoExiste() {
        when(ticketRepository.findById(10L)).thenReturn(Optional.of(ticket));
        when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

        requestDTO.setCategoriaId(99L);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            ticketService.actualizar(10L, requestDTO);
        });

        assertTrue(exception.getMessage().contains("Categoría NO encontrada"));
    }

    @Test
    public void testEliminar() {
        doNothing().when(ticketRepository).deleteById(10L);

        ticketService.eliminar(10L);

        verify(ticketRepository, times(1)).deleteById(10L);
    }

    @Test
    public void testBuscarPorTipo() {
        when(ticketRepository.findByTipoContainingIgnoreCase("vi")).thenReturn(Arrays.asList(ticket));

        List<TicketResponseDTO> result = ticketService.buscarPorTipo("vi");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("VIP", result.get(0).getTipo());
    }

    @Test
    public void testBuscarPorCategoria() {
        when(ticketRepository.findByCategoriaId(1L)).thenReturn(Arrays.asList(ticket));

        List<TicketResponseDTO> result = ticketService.buscarPorCategoria(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Conciertos", result.get(0).getCategoriaNombre());
    }

    @Test
    public void testBuscarBajoPresupuesto() {
        when(ticketRepository.findTicketsBajoPresupuesto(new BigDecimal("60000"))).thenReturn(Arrays.asList(ticket));

        List<TicketResponseDTO> result = ticketService.buscarBajoPresupuesto(new BigDecimal("60000"));

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("VIP", result.get(0).getTipo());
    }

    @Test
    public void testActualizarSoloStock_CuandoExiste() {
        when(ticketRepository.findById(10L)).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);

        ticketService.actualizarSoloStock(10L, 50);

        verify(ticketRepository, times(1)).save(any(Ticket.class));
        assertEquals(50, ticket.getStock());
    }

    @Test
    public void testActualizarSoloStock_CuandoNoExiste() {
        when(ticketRepository.findById(99L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            ticketService.actualizarSoloStock(99L, 50);
        });

        assertTrue(exception.getMessage().contains("Ticket no encontrado"));
    }
}
