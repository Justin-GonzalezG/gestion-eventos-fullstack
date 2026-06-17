package cl.eventos.ms_tickets.service;

import cl.eventos.ms_tickets.model.Categoria;
import cl.eventos.ms_tickets.repository.CategoriaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
public class CategoriaServiceTest {

    @Autowired
    private CategoriaService categoriaService;

    @MockBean
    private CategoriaRepository categoriaRepository;

    private Categoria categoria;

    @BeforeEach
    void setUp() {
        categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNombre("Conciertos");
        categoria.setDescripcion("Eventos musicales");
    }

    @Test
    public void testObtenerTodas() {
        when(categoriaRepository.findAll()).thenReturn(Arrays.asList(categoria));

        List<Categoria> result = categoriaService.obtenerTodas();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Conciertos", result.get(0).getNombre());
        verify(categoriaRepository, times(1)).findAll();
    }

    @Test
    public void testObtenerPorId_CuandoExiste() {
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));

        Optional<Categoria> result = categoriaService.obtenerPorId(1L);

        assertTrue(result.isPresent());
        assertEquals("Conciertos", result.get().getNombre());
    }

    @Test
    public void testObtenerPorId_CuandoNoExiste() {
        when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Categoria> result = categoriaService.obtenerPorId(99L);

        assertFalse(result.isPresent());
    }

    @Test
    public void testGuardar() {
        when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoria);

        Categoria result = categoriaService.guardar(categoria);

        assertNotNull(result);
        assertEquals("Conciertos", result.getNombre());
        verify(categoriaRepository, times(1)).save(any(Categoria.class));
    }

    @Test
    public void testEliminar() {
        doNothing().when(categoriaRepository).deleteById(1L);

        categoriaService.eliminar(1L);

        verify(categoriaRepository, times(1)).deleteById(1L);
    }
}
