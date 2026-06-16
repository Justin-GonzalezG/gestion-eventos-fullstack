package cl.eventos.ms_recomendacion.service;

import cl.eventos.ms_recomendacion.client.EventoClient;
import cl.eventos.ms_recomendacion.client.UsuarioClient;
import cl.eventos.ms_recomendacion.dto.EventoDTO;
import cl.eventos.ms_recomendacion.dto.UsuarioDTO;
import cl.eventos.ms_recomendacion.dto.RecomendacionConDetalleDTO;
import cl.eventos.ms_recomendacion.dto.RecomendacionRequestDTO;
import cl.eventos.ms_recomendacion.dto.RecomendacionResponseDTO;
import cl.eventos.ms_recomendacion.model.Recomendacion;
import cl.eventos.ms_recomendacion.repository.RecomendacionRepository;
import feign.FeignException;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
public class RecomendacionServiceTest {

    @Autowired
    private RecomendacionService recomendacionService;

    @MockBean
    private RecomendacionRepository recomendacionRepository;

    @MockBean
    private EventoClient eventoClient;

    @MockBean
    private UsuarioClient usuarioClient;

    private Recomendacion recomendacion;
    private RecomendacionRequestDTO requestDTO;
    private EventoDTO eventoDTO;
    private UsuarioDTO usuarioDTO;

    @BeforeEach
    void setUp() {
        recomendacion = new Recomendacion();
        recomendacion.setId(1L);
        recomendacion.setUsuarioId(10L);
        recomendacion.setEventoSugeridoId(100L);
        recomendacion.setMotivo("Porque te gusta el rock");
        recomendacion.setNivelAfinidad(95);
        recomendacion.setFechaRecomendacion(new Date());

        requestDTO = new RecomendacionRequestDTO();
        requestDTO.setUsuarioId(10L);
        requestDTO.setEventoSugeridoId(100L);
        requestDTO.setMotivo("Porque te gusta el rock");
        requestDTO.setNivelAfinidad(95);

        eventoDTO = new EventoDTO(100L, "Concierto Rock", "Un gran concierto", new Date(), "Estadio", 500, "ACTIVO");
        usuarioDTO = new UsuarioDTO("12345678-9", "Juan", "Perez", "juan@rock.com");
    }

    @Test
    public void testObtenerTodas() {
        when(recomendacionRepository.findAll()).thenReturn(Arrays.asList(recomendacion));

        List<RecomendacionResponseDTO> result = recomendacionService.obtenerTodas();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Porque te gusta el rock", result.get(0).getMotivo());
        verify(recomendacionRepository, times(1)).findAll();
    }

    @Test
    public void testObtenerPorId_CuandoExiste() {
        when(recomendacionRepository.findById(1L)).thenReturn(Optional.of(recomendacion));

        Optional<RecomendacionResponseDTO> result = recomendacionService.obtenerPorId(1L);

        assertTrue(result.isPresent());
        assertEquals(95, result.get().getNivelAfinidad());
    }

    @Test
    public void testObtenerPorId_CuandoNoExiste() {
        when(recomendacionRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<RecomendacionResponseDTO> result = recomendacionService.obtenerPorId(99L);

        assertFalse(result.isPresent());
    }

    @Test
    public void testGuardar() {
        when(recomendacionRepository.save(any(Recomendacion.class))).thenReturn(recomendacion);

        RecomendacionResponseDTO result = recomendacionService.guardar(requestDTO);

        assertNotNull(result);
        assertEquals(10L, result.getUsuarioId());
        verify(recomendacionRepository, times(1)).save(any(Recomendacion.class));
    }

    @Test
    public void testActualizar_CuandoExiste() {
        when(recomendacionRepository.findById(1L)).thenReturn(Optional.of(recomendacion));
        when(recomendacionRepository.save(any(Recomendacion.class))).thenReturn(recomendacion);

        Optional<RecomendacionResponseDTO> result = recomendacionService.actualizar(1L, requestDTO);

        assertTrue(result.isPresent());
        assertEquals(10L, result.get().getUsuarioId());
    }

    @Test
    public void testActualizar_CuandoNoExiste() {
        when(recomendacionRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<RecomendacionResponseDTO> result = recomendacionService.actualizar(99L, requestDTO);

        assertFalse(result.isPresent());
        verify(recomendacionRepository, never()).save(any(Recomendacion.class));
    }

    @Test
    public void testEliminar() {
        doNothing().when(recomendacionRepository).deleteById(1L);

        recomendacionService.eliminar(1L);

        verify(recomendacionRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testObtenerDetalleCompleto_ExitoTotal() {
        when(recomendacionRepository.findById(1L)).thenReturn(Optional.of(recomendacion));
        when(eventoClient.obtenerEventoPorId(100L)).thenReturn(ResponseEntity.ok(eventoDTO));
        when(usuarioClient.obtenerUsuarioPorId(10L)).thenReturn(ResponseEntity.ok(usuarioDTO));

        Optional<RecomendacionConDetalleDTO> result = recomendacionService.obtenerDetalleCompleto(1L);

        assertTrue(result.isPresent());
        assertNotNull(result.get().getDetalleEvento());
        assertEquals("Concierto Rock", result.get().getDetalleEvento().getNombre());
        assertNotNull(result.get().getDetalleUsuario());
        assertEquals("Juan", result.get().getDetalleUsuario().getNombre());
    }

    @Test
    public void testObtenerDetalleCompleto_FallaEventoYUsuario() {
        when(recomendacionRepository.findById(1L)).thenReturn(Optional.of(recomendacion));
        when(eventoClient.obtenerEventoPorId(100L)).thenThrow(mock(FeignException.class));
        when(usuarioClient.obtenerUsuarioPorId(10L)).thenThrow(mock(FeignException.class));

        Optional<RecomendacionConDetalleDTO> result = recomendacionService.obtenerDetalleCompleto(1L);

        assertTrue(result.isPresent());
        assertNull(result.get().getDetalleEvento());
        assertNull(result.get().getDetalleUsuario());
        assertEquals(95, result.get().getNivelAfinidad());
    }
}
