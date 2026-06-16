package cl.eventos.ms_pagos.service;

import cl.eventos.ms_pagos.client.AuthClient;
import cl.eventos.ms_pagos.client.OrdenClient;
import cl.eventos.ms_pagos.dto.OrdenDTO;
import cl.eventos.ms_pagos.dto.PagoRequestDTO;
import cl.eventos.ms_pagos.dto.PagoResponseDTO;
import cl.eventos.ms_pagos.model.Pago;
import cl.eventos.ms_pagos.repository.PagoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
public class PagoServiceTest {

    @Autowired
    private PagoService pagoService;

    @MockBean
    private PagoRepository pagoRepository;

    @MockBean
    private AuthClient authClient;

    @MockBean
    private OrdenClient ordenClient;

    private PagoRequestDTO requestDTO;
    private Pago pago;
    private OrdenDTO ordenDTO;
    private Map<String, Object> tokenValido;

    @BeforeEach
    void setUp() {
        requestDTO = new PagoRequestDTO();
        requestDTO.setOrdenId(10L);
        requestDTO.setMonto(new BigDecimal("100000"));
        requestDTO.setMetodoPago("TARJETA");

        pago = new Pago();
        pago.setId(1L);
        pago.setOrdenId(10L);
        pago.setMonto(new BigDecimal("100000"));
        pago.setMetodoPago("TARJETA");
        pago.setEstadoPago("APROBADO");
        pago.setFechaPago(LocalDateTime.now());

        ordenDTO = new OrdenDTO();
        ordenDTO.setId(10L);
        ordenDTO.setGranTotal(new BigDecimal("100000"));

        tokenValido = new HashMap<>();
        tokenValido.put("valido", true);
    }

    @Test
    public void testSave_Exito() {
        when(authClient.validarToken("valid-token")).thenReturn(tokenValido);
        when(ordenClient.buscarPorId(10L)).thenReturn(ordenDTO);
        doNothing().when(ordenClient).actualizar(10L, "PAGADA");
        when(pagoRepository.save(any(Pago.class))).thenReturn(pago);

        PagoResponseDTO result = pagoService.save(requestDTO, "valid-token");

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("APROBADO", result.getEstadoPago());
        verify(ordenClient, times(1)).actualizar(10L, "PAGADA");
        verify(pagoRepository, times(1)).save(any(Pago.class));
    }

    @Test
    public void testSave_TokenInvalido() {
        Map<String, Object> tokenInvalido = new HashMap<>();
        tokenInvalido.put("valido", false);

        when(authClient.validarToken("invalid-token")).thenReturn(tokenInvalido);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            pagoService.save(requestDTO, "invalid-token");
        });

        assertEquals("Acceso denegado. Token inválido o expirado.", exception.getMessage());
    }

    @Test
    public void testSave_TokenNulo() {
        when(authClient.validarToken("invalid-token")).thenReturn(null);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            pagoService.save(requestDTO, "invalid-token");
        });

        assertEquals("Acceso denegado. Token inválido o expirado.", exception.getMessage());
    }

    @Test
    public void testSave_MontoInsuficiente() {
        requestDTO.setMonto(new BigDecimal("50000")); // Menor que 100000

        when(authClient.validarToken("valid-token")).thenReturn(tokenValido);
        when(ordenClient.buscarPorId(10L)).thenReturn(ordenDTO);
        doNothing().when(ordenClient).actualizar(10L, "RECHAZADO");

        Exception exception = assertThrows(RuntimeException.class, () -> {
            pagoService.save(requestDTO, "valid-token");
        });

        assertEquals("Pago RECHAZADO. Motivo: Monto insuficiente. La orden requiere: $100000", exception.getMessage());
        verify(ordenClient, times(1)).actualizar(10L, "RECHAZADO");
    }

    @Test
    public void testSave_MontoNulo() {
        requestDTO.setMonto(null);

        when(authClient.validarToken("valid-token")).thenReturn(tokenValido);
        when(ordenClient.buscarPorId(10L)).thenReturn(ordenDTO);
        doNothing().when(ordenClient).actualizar(10L, "RECHAZADO");

        Exception exception = assertThrows(RuntimeException.class, () -> {
            pagoService.save(requestDTO, "valid-token");
        });

        assertEquals("Pago RECHAZADO. Motivo: Monto insuficiente. La orden requiere: $100000", exception.getMessage());
        verify(ordenClient, times(1)).actualizar(10L, "RECHAZADO");
    }

    @Test
    public void testUpdate_CuandoExiste() {
        when(pagoRepository.findById(1L)).thenReturn(Optional.of(pago));
        when(pagoRepository.save(any(Pago.class))).thenReturn(pago);

        requestDTO.setMonto(new BigDecimal("120000"));
        PagoResponseDTO result = pagoService.update(1L, requestDTO);

        assertNotNull(result);
        assertEquals(new BigDecimal("120000"), result.getMonto());
    }

    @Test
    public void testUpdate_CuandoNoExiste() {
        when(pagoRepository.findById(99L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            pagoService.update(99L, requestDTO);
        });

        assertEquals("Pago no encontrado", exception.getMessage());
    }

    @Test
    public void testUpdateOptional_CuandoExiste() {
        when(pagoRepository.findById(1L)).thenReturn(Optional.of(pago));
        when(pagoRepository.save(any(Pago.class))).thenReturn(pago);

        Optional<PagoResponseDTO> result = pagoService.updateOptional(1L, requestDTO);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
    }

    @Test
    public void testUpdateOptional_CuandoNoExiste() {
        when(pagoRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<PagoResponseDTO> result = pagoService.updateOptional(99L, requestDTO);

        assertFalse(result.isPresent());
    }

    @Test
    public void testActualizarEstado_CuandoExiste() {
        when(pagoRepository.findById(1L)).thenReturn(Optional.of(pago));
        when(pagoRepository.save(any(Pago.class))).thenReturn(pago);

        PagoResponseDTO result = pagoService.actualizarEstado(1L, "rechazado");

        assertNotNull(result);
        assertEquals("RECHAZADO", result.getEstadoPago());
    }

    @Test
    public void testActualizarEstado_CuandoNoExiste() {
        when(pagoRepository.findById(99L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            pagoService.actualizarEstado(99L, "RECHAZADO");
        });

        assertEquals("Pago no encontrado", exception.getMessage());
    }

    @Test
    public void testActualizarEstadoOptional_CuandoExiste() {
        when(pagoRepository.findById(1L)).thenReturn(Optional.of(pago));
        when(pagoRepository.save(any(Pago.class))).thenReturn(pago);

        Optional<PagoResponseDTO> result = pagoService.actualizarEstadoOptional(1L, "rechazado");

        assertTrue(result.isPresent());
        assertEquals("RECHAZADO", result.get().getEstadoPago());
    }

    @Test
    public void testActualizarEstadoOptional_CuandoNoExiste() {
        when(pagoRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<PagoResponseDTO> result = pagoService.actualizarEstadoOptional(99L, "RECHAZADO");

        assertFalse(result.isPresent());
    }

    @Test
    public void testFindAll() {
        when(pagoRepository.findAll()).thenReturn(Arrays.asList(pago));

        List<PagoResponseDTO> result = pagoService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }

    @Test
    public void testFindById_CuandoExiste() {
        when(pagoRepository.findById(1L)).thenReturn(Optional.of(pago));

        PagoResponseDTO result = pagoService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    public void testFindById_CuandoNoExiste() {
        when(pagoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> {
            pagoService.findById(99L);
        });
    }

    @Test
    public void testFindByIdOptional_CuandoExiste() {
        when(pagoRepository.findById(1L)).thenReturn(Optional.of(pago));

        Optional<PagoResponseDTO> result = pagoService.findByIdOptional(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
    }

    @Test
    public void testFindByIdOptional_CuandoNoExiste() {
        when(pagoRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<PagoResponseDTO> result = pagoService.findByIdOptional(99L);

        assertFalse(result.isPresent());
    }

    @Test
    public void testExistsById() {
        when(pagoRepository.existsById(1L)).thenReturn(true);

        boolean result = pagoService.existsById(1L);

        assertTrue(result);
    }

    @Test
    public void testDelete() {
        doNothing().when(pagoRepository).deleteById(1L);

        pagoService.delete(1L);

        verify(pagoRepository, times(1)).deleteById(1L);
    }
}
