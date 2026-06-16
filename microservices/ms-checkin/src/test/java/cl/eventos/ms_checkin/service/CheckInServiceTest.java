package cl.eventos.ms_checkin.service;

import cl.eventos.ms_checkin.client.OrdenClient;
import cl.eventos.ms_checkin.client.TicketClient;
import cl.eventos.ms_checkin.dto.CheckInRequestDTO;
import cl.eventos.ms_checkin.dto.CheckInResponseDTO;
import cl.eventos.ms_checkin.model.CheckIn;
import cl.eventos.ms_checkin.repository.CheckInRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
public class CheckInServiceTest {

    @Autowired
    private CheckInService checkInService;

    @MockBean
    private CheckInRepository checkInRepository;

    @MockBean
    private TicketClient ticketClient;

    @MockBean
    private OrdenClient ordenClient;

    private CheckIn checkIn;
    private CheckInRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        checkIn = new CheckIn();
        checkIn.setId(1L);
        checkIn.setTicketId(10L);
        checkIn.setFechaIngreso(LocalDateTime.now());

        requestDTO = new CheckInRequestDTO();
        requestDTO.setTicketId(10L);
    }

    @Test
    public void testObtenerTodos() {
        when(checkInRepository.findAll()).thenReturn(Arrays.asList(checkIn));

        List<CheckInResponseDTO> result = checkInService.obtenerTodos();

        assertEquals(1, result.size());
        assertEquals(10L, result.get(0).getTicketId());
    }

    @Test
    public void testObtenerPorId_CuandoExiste() {
        when(checkInRepository.findById(1L)).thenReturn(Optional.of(checkIn));

        Optional<CheckInResponseDTO> result = checkInService.obtenerPorId(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
    }

    @Test
    public void testObtenerPorId_CuandoNoExiste() {
        when(checkInRepository.findById(99L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            checkInService.obtenerPorId(99L);
        });

        assertEquals("El registro de check-in con la ID 99 no existe", exception.getMessage());
    }

    @Test
    public void testActualizar_CuandoExiste() {
        when(checkInRepository.findById(1L)).thenReturn(Optional.of(checkIn));
        when(checkInRepository.save(any(CheckIn.class))).thenReturn(checkIn);

        requestDTO.setTicketId(20L);
        Optional<CheckInResponseDTO> result = checkInService.actualizar(1L, requestDTO);

        assertTrue(result.isPresent());
        assertEquals(20L, checkIn.getTicketId()); // el mock cambia por referencia
    }

    @Test
    public void testActualizar_CuandoNoExiste() {
        when(checkInRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<CheckInResponseDTO> result = checkInService.actualizar(99L, requestDTO);

        assertFalse(result.isPresent());
    }

    @Test
    public void testEliminar() {
        doNothing().when(checkInRepository).deleteById(1L);

        checkInService.eliminar(1L);

        verify(checkInRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testBuscarPorTicket_CuandoExiste() {
        when(checkInRepository.buscarPorTicketId(10L)).thenReturn(Optional.of(checkIn));

        Optional<CheckInResponseDTO> result = checkInService.buscarPorTicket(10L);

        assertTrue(result.isPresent());
        assertEquals(10L, result.get().getTicketId());
    }

    @Test
    public void testBuscarPorTicket_CuandoNoExiste() {
        when(checkInRepository.buscarPorTicketId(99L)).thenReturn(Optional.empty());

        Optional<CheckInResponseDTO> result = checkInService.buscarPorTicket(99L);

        assertFalse(result.isPresent());
    }

    @Test
    public void testObtenerTotalAsistentes() {
        when(checkInRepository.contarTotalIngresos()).thenReturn(5L);

        Long result = checkInService.obtenerTotalAsistentes();

        assertEquals(5L, result);
    }

    @Test
    public void testRegistrarIngreso_Exito() {
        when(ticketClient.validarTicket(10L)).thenReturn(org.springframework.http.ResponseEntity.ok().build());
        when(checkInRepository.existsByTicketId(10L)).thenReturn(false);
        when(ordenClient.verificarPagoTicket(10L)).thenReturn(true);
        when(checkInRepository.save(any(CheckIn.class))).thenReturn(checkIn);

        CheckInResponseDTO result = checkInService.registrarIngreso(requestDTO);

        assertNotNull(result);
        assertEquals(10L, result.getTicketId());
    }

    @Test
    public void testRegistrarIngreso_TicketInvalido() {
        when(ticketClient.validarTicket(10L)).thenThrow(new RuntimeException("Ticket Falso"));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            checkInService.registrarIngreso(requestDTO);
        });

        assertEquals("Ticket Falso", exception.getMessage());
        verify(checkInRepository, never()).save(any(CheckIn.class));
    }

    @Test
    public void testRegistrarIngreso_TicketYaUtilizado() {
        when(ticketClient.validarTicket(10L)).thenReturn(org.springframework.http.ResponseEntity.ok().build());
        when(checkInRepository.existsByTicketId(10L)).thenReturn(true);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            checkInService.registrarIngreso(requestDTO);
        });

        assertEquals("ACCESO DENEGADO: El ticket ya fue utilizado.", exception.getMessage());
        verify(checkInRepository, never()).save(any(CheckIn.class));
    }

    @Test
    public void testRegistrarIngreso_TicketNoPagado() {
        when(ticketClient.validarTicket(10L)).thenReturn(org.springframework.http.ResponseEntity.ok().build());
        when(checkInRepository.existsByTicketId(10L)).thenReturn(false);
        when(ordenClient.verificarPagoTicket(10L)).thenReturn(false);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            checkInService.registrarIngreso(requestDTO);
        });

        assertEquals("ACCESO DENEGADO: El ticket no figura como PAGADO.", exception.getMessage());
        verify(checkInRepository, never()).save(any(CheckIn.class));
    }
}
