package cl.eventos.ms_eventos.service;

import cl.eventos.ms_eventos.client.TicketClient;
import cl.eventos.ms_eventos.dto.EventoRequestDTO;
import cl.eventos.ms_eventos.dto.EventoResponseDTO;
import cl.eventos.ms_eventos.dto.TicketDTO;
import cl.eventos.ms_eventos.model.Evento;
import cl.eventos.ms_eventos.repository.EventoRepository;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
public class EventoServiceTest {

    @Autowired
    private EventoService eventoService;

    @MockBean
    private EventoRepository eventoRepository;

    @MockBean
    private TicketClient ticketClient;

    private Evento evento;
    private EventoRequestDTO requestDTO;
    private TicketDTO ticketDTO;

    @BeforeEach
    void setUp() {
        evento = new Evento();
        evento.setId(1L);
        evento.setNombre("Concierto Rock");
        evento.setInformacionGeneral("Un gran concierto");
        evento.setFechaHora(new Date());
        evento.setUbicacion("Estadio");
        evento.setCapacidadMaxima(500);
        evento.setEstado("ACTIVO");

        requestDTO = new EventoRequestDTO();
        requestDTO.setNombre("Concierto Rock");
        requestDTO.setInformacionGeneral("Un gran concierto");
        requestDTO.setFechaHora(new Date());
        requestDTO.setUbicacion("Estadio");
        requestDTO.setCapacidadMaxima(500);
        requestDTO.setEstado("ACTIVO");

        ticketDTO = new TicketDTO(10L, "VIP", new BigDecimal("50000"), 100);
    }

    @Test
    public void testObtenerTodos() {
        when(eventoRepository.findAll()).thenReturn(Arrays.asList(evento));

        List<EventoResponseDTO> result = eventoService.obtenerTodos();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Concierto Rock", result.get(0).getNombre());
        verify(eventoRepository, times(1)).findAll();
    }

    @Test
    public void testObtenerPorId_ConTickets_Exito() {
        when(eventoRepository.findById(1L)).thenReturn(Optional.of(evento));
        when(ticketClient.buscarPorCategoria(1L)).thenReturn(Arrays.asList(ticketDTO));

        Optional<EventoResponseDTO> result = eventoService.obtenerPorId(1L);

        assertTrue(result.isPresent());
        assertEquals("Concierto Rock", result.get().getNombre());
        assertNotNull(result.get().getTickets());
        assertEquals(1, result.get().getTickets().size());
        assertEquals("VIP", result.get().getTickets().get(0).getTipo());
    }

    @Test
    public void testObtenerPorId_SinTickets_FallaFeign() {
        when(eventoRepository.findById(1L)).thenReturn(Optional.of(evento));
        when(ticketClient.buscarPorCategoria(1L)).thenThrow(mock(FeignException.class));

        Optional<EventoResponseDTO> result = eventoService.obtenerPorId(1L);

        assertTrue(result.isPresent());
        assertEquals("Concierto Rock", result.get().getNombre());
        assertNotNull(result.get().getTickets());
        assertTrue(result.get().getTickets().isEmpty());
    }

    @Test
    public void testObtenerPorId_NoExiste() {
        when(eventoRepository.findById(99L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            eventoService.obtenerPorId(99L);
        });

        assertTrue(exception.getMessage().contains("no existe"));
    }

    @Test
    public void testGuardar() {
        when(eventoRepository.save(any(Evento.class))).thenReturn(evento);

        EventoResponseDTO result = eventoService.guardar(requestDTO);

        assertNotNull(result);
        assertEquals("Concierto Rock", result.getNombre());
        verify(eventoRepository, times(1)).save(any(Evento.class));
    }

    @Test
    public void testActualizar_CuandoExiste() {
        when(eventoRepository.findById(1L)).thenReturn(Optional.of(evento));
        when(eventoRepository.save(any(Evento.class))).thenReturn(evento);

        Optional<EventoResponseDTO> result = eventoService.actualizar(1L, requestDTO);

        assertTrue(result.isPresent());
        assertEquals("Concierto Rock", result.get().getNombre());
    }

    @Test
    public void testActualizar_CuandoNoExiste() {
        when(eventoRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<EventoResponseDTO> result = eventoService.actualizar(99L, requestDTO);

        assertFalse(result.isPresent());
        verify(eventoRepository, never()).save(any(Evento.class));
    }

    @Test
    public void testEliminar() {
        doNothing().when(eventoRepository).deleteById(1L);

        eventoService.eliminar(1L);

        verify(eventoRepository, times(1)).deleteById(1L);
    }
}
