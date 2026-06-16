package cl.eventos.ms_reportes.service;

import cl.eventos.ms_reportes.client.OrdenClient;
import cl.eventos.ms_reportes.client.PagoClient;
import cl.eventos.ms_reportes.client.TicketClient;
import cl.eventos.ms_reportes.dto.*;
import cl.eventos.ms_reportes.model.Reporte;
import cl.eventos.ms_reportes.repository.ReporteRepository;
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
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
public class ReporteServiceTest {

    @Autowired
    private ReporteService reporteService;

    @MockBean
    private ReporteRepository reporteRepository;

    @MockBean
    private PagoClient pagoClient;

    @MockBean
    private OrdenClient ordenClient;

    @MockBean
    private TicketClient ticketClient;

    private Reporte reporte;
    private ReporteRequestDTO requestDTO;
    private PagoDTO pagoAprobado;
    private PagoDTO pagoRechazado;
    private OrdenDTO ordenConDetalles;
    private OrdenDTO ordenSinDetalles;
    private DetalleOrdenDTO detalleNormal;
    private DetalleOrdenDTO detalleNulls;

    @BeforeEach
    void setUp() {
        reporte = new Reporte();
        reporte.setId(1L);
        reporte.setPeriodo("Junio 2026");
        reporte.setIngresosTotales(new BigDecimal("1000"));
        reporte.setTotalTicketsVendidos(5);
        reporte.setTicketMasPopular("VIP");

        requestDTO = new ReporteRequestDTO();
        requestDTO.setPeriodo("Junio 2026");
        requestDTO.setIngresosTotales(new BigDecimal("1000"));
        requestDTO.setTotalTicketsVendidos(5);
        requestDTO.setTicketMasPopular("VIP");

        pagoAprobado = new PagoDTO();
        pagoAprobado.setEstadoPago("APROBADO");
        pagoAprobado.setMonto(new BigDecimal("1000"));

        pagoRechazado = new PagoDTO();
        pagoRechazado.setEstadoPago("RECHAZADO");
        pagoRechazado.setMonto(new BigDecimal("500"));

        detalleNormal = new DetalleOrdenDTO();
        detalleNormal.setTicketId(1L);
        detalleNormal.setCantidad(2);

        detalleNulls = new DetalleOrdenDTO();
        detalleNulls.setTicketId(null);
        detalleNulls.setCantidad(null);

        ordenConDetalles = new OrdenDTO();
        ordenConDetalles.setDetalles(Arrays.asList(detalleNormal, detalleNulls));

        ordenSinDetalles = new OrdenDTO();
        ordenSinDetalles.setDetalles(null);
    }

    @Test
    public void testGuardar_FlujoCompleto() {
        when(pagoClient.listarPagos()).thenReturn(Arrays.asList(pagoAprobado, pagoRechazado, new PagoDTO()));
        when(ordenClient.listarOrdenes()).thenReturn(Arrays.asList(ordenConDetalles, ordenSinDetalles));
        
        TicketDTO ticketDTO = new TicketDTO();
        ticketDTO.setId(1L);
        ticketDTO.setTipo("VIP");
        when(ticketClient.obtenerTicketPorId(1L)).thenReturn(ticketDTO);

        when(reporteRepository.save(any(Reporte.class))).thenReturn(reporte);

        ReporteResponseDTO result = reporteService.guardar(requestDTO);

        assertNotNull(result);
        assertEquals(new BigDecimal("1000"), result.getIngresosTotales());
        assertEquals(5, result.getTotalTicketsVendidos()); // as per setup of `reporte` mock
        assertEquals("VIP", result.getTicketMasPopular());
    }

    @Test
    public void testGuardar_TicketDtoTipoNull() {
        when(pagoClient.listarPagos()).thenReturn(Arrays.asList(pagoAprobado));
        when(ordenClient.listarOrdenes()).thenReturn(Arrays.asList(ordenConDetalles));

        TicketDTO ticketDTO = new TicketDTO();
        ticketDTO.setId(1L);
        ticketDTO.setTipo(null);
        when(ticketClient.obtenerTicketPorId(1L)).thenReturn(ticketDTO);

        when(reporteRepository.save(any(Reporte.class))).thenReturn(reporte);

        reporteService.guardar(requestDTO);
        verify(reporteRepository, times(1)).save(any(Reporte.class));
    }

    @Test
    public void testGuardar_TicketDtoNull() {
        when(pagoClient.listarPagos()).thenReturn(Arrays.asList(pagoAprobado));
        when(ordenClient.listarOrdenes()).thenReturn(Arrays.asList(ordenConDetalles));

        when(ticketClient.obtenerTicketPorId(1L)).thenReturn(null);

        when(reporteRepository.save(any(Reporte.class))).thenReturn(reporte);

        reporteService.guardar(requestDTO);
        verify(reporteRepository, times(1)).save(any(Reporte.class));
    }

    @Test
    public void testGuardar_ExcepcionEnPagoClient() {
        when(pagoClient.listarPagos()).thenThrow(new RuntimeException("Error Pagos"));
        when(ordenClient.listarOrdenes()).thenReturn(Collections.emptyList());
        when(reporteRepository.save(any(Reporte.class))).thenReturn(reporte);

        ReporteResponseDTO result = reporteService.guardar(requestDTO);

        assertNotNull(result);
        verify(reporteRepository, times(1)).save(any(Reporte.class));
    }

    @Test
    public void testGuardar_ExcepcionEnOrdenClient() {
        when(pagoClient.listarPagos()).thenReturn(Collections.emptyList());
        when(ordenClient.listarOrdenes()).thenThrow(new RuntimeException("Error Ordenes"));
        when(reporteRepository.save(any(Reporte.class))).thenReturn(reporte);

        ReporteResponseDTO result = reporteService.guardar(requestDTO);

        assertNotNull(result);
        verify(reporteRepository, times(1)).save(any(Reporte.class));
    }

    @Test
    public void testGuardar_ExcepcionEnTicketClient() {
        when(pagoClient.listarPagos()).thenReturn(Collections.emptyList());
        when(ordenClient.listarOrdenes()).thenReturn(Arrays.asList(ordenConDetalles));
        when(ticketClient.obtenerTicketPorId(1L)).thenThrow(new RuntimeException("Error Tickets"));
        when(reporteRepository.save(any(Reporte.class))).thenReturn(reporte);

        ReporteResponseDTO result = reporteService.guardar(requestDTO);

        assertNotNull(result);
        verify(reporteRepository, times(1)).save(any(Reporte.class));
    }

    @Test
    public void testObtenerTodos() {
        when(reporteRepository.findAll()).thenReturn(Arrays.asList(reporte));

        List<ReporteResponseDTO> result = reporteService.obtenerTodos();

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }

    @Test
    public void testObtenerPorId_CuandoExiste() {
        when(reporteRepository.findById(1L)).thenReturn(Optional.of(reporte));

        Optional<ReporteResponseDTO> result = reporteService.obtenerPorId(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
    }

    @Test
    public void testObtenerPorId_CuandoNoExiste() {
        when(reporteRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<ReporteResponseDTO> result = reporteService.obtenerPorId(99L);

        assertFalse(result.isPresent());
    }

    @Test
    public void testActualizar_CuandoExiste() {
        when(reporteRepository.findById(1L)).thenReturn(Optional.of(reporte));
        when(reporteRepository.save(any(Reporte.class))).thenReturn(reporte);

        Optional<ReporteResponseDTO> result = reporteService.actualizar(1L, requestDTO);

        assertTrue(result.isPresent());
        assertEquals("Junio 2026", result.get().getPeriodo());
    }

    @Test
    public void testActualizar_CuandoNoExiste() {
        when(reporteRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<ReporteResponseDTO> result = reporteService.actualizar(99L, requestDTO);

        assertFalse(result.isPresent());
    }

    @Test
    public void testEliminar() {
        doNothing().when(reporteRepository).deleteById(1L);

        reporteService.eliminar(1L);

        verify(reporteRepository, times(1)).deleteById(1L);
    }
}
