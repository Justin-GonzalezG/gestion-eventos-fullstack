package cl.eventos.ms_tickets.controller;

import cl.eventos.ms_tickets.model.Categoria;
import cl.eventos.ms_tickets.service.CategoriaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoriaController.class)
public class CategoriaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoriaService categoriaService;

    @Autowired
    private ObjectMapper objectMapper;

    private Categoria categoria;

    @BeforeEach
    void setUp() {
        categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNombre("Conciertos");
        categoria.setDescripcion("Eventos musicales");
    }

    @Test
    public void testObtenerTodas() throws Exception {
        when(categoriaService.obtenerTodas()).thenReturn(Arrays.asList(categoria));

        mockMvc.perform(get("/api/categorias/listar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Conciertos"));
    }

    @Test
    public void testObtenerTodas_Vacio() throws Exception {
        when(categoriaService.obtenerTodas()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/categorias/listar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    public void testObtenerPorId_CuandoExiste() throws Exception {
        when(categoriaService.obtenerPorId(1L)).thenReturn(Optional.of(categoria));

        mockMvc.perform(get("/api/categorias/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Conciertos"));
    }

    @Test
    public void testObtenerPorId_CuandoNoExiste() throws Exception {
        when(categoriaService.obtenerPorId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/categorias/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testCrear() throws Exception {
        when(categoriaService.guardar(any(Categoria.class))).thenReturn(categoria);

        mockMvc.perform(post("/api/categorias/crear")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoria)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.mensaje").value("La categoría fue creada con éxito"))
                .andExpect(jsonPath("$.categoria.nombre").value("Conciertos"));
    }

    @Test
    public void testActualizar_CuandoExiste() throws Exception {
        when(categoriaService.obtenerPorId(1L)).thenReturn(Optional.of(categoria));
        when(categoriaService.guardar(any(Categoria.class))).thenReturn(categoria);

        mockMvc.perform(put("/api/categorias/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoria)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Categoría actualizada correctamente"))
                .andExpect(jsonPath("$.categoria.nombre").value("Conciertos"));
    }

    @Test
    public void testActualizar_CuandoNoExiste() throws Exception {
        when(categoriaService.obtenerPorId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/categorias/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoria)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testEliminar_CuandoExiste() throws Exception {
        when(categoriaService.obtenerPorId(1L)).thenReturn(Optional.of(categoria));
        doNothing().when(categoriaService).eliminar(1L);

        mockMvc.perform(delete("/api/categorias/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Categoría eliminada correctamente."));
    }

    @Test
    public void testEliminar_CuandoNoExiste() throws Exception {
        when(categoriaService.obtenerPorId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/categorias/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("No se encontró la categoría con ID: 99"));
    }
}
