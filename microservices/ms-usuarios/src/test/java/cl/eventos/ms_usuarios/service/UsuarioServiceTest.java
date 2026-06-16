package cl.eventos.ms_usuarios.service;

import cl.eventos.ms_usuarios.client.AuthClient;
import cl.eventos.ms_usuarios.dto.AuthDTO;
import cl.eventos.ms_usuarios.dto.UsuarioConAuthDTO;
import cl.eventos.ms_usuarios.dto.UsuarioRequestDTO;
import cl.eventos.ms_usuarios.dto.UsuarioResponseDTO;
import cl.eventos.ms_usuarios.model.Usuario;
import cl.eventos.ms_usuarios.repository.UsuarioRepository;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
public class UsuarioServiceTest {

    @Autowired
    private UsuarioService usuarioService;

    @MockBean
    private UsuarioRepository usuarioRepository;

    @MockBean
    private AuthClient authClient;

    private Usuario usuario;
    private UsuarioRequestDTO usuarioRequestDTO;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setRun("12345678-9");
        usuario.setNombre("Juan");
        usuario.setApellido("Perez");
        usuario.setEmail("juan@test.com");
        usuario.setTelefono("987654321");
        usuario.setDireccion("Calle Falsa 123");

        usuarioRequestDTO = new UsuarioRequestDTO();
        usuarioRequestDTO.setRun("12345678-9");
        usuarioRequestDTO.setNombre("Juan");
        usuarioRequestDTO.setApellido("Perez");
        usuarioRequestDTO.setEmail("juan@test.com");
        usuarioRequestDTO.setTelefono("987654321");
        usuarioRequestDTO.setDireccion("Calle Falsa 123");
    }

    @Test
    public void testObtenerTodos() {
        when(usuarioRepository.findAll()).thenReturn(Arrays.asList(usuario));

        List<UsuarioResponseDTO> resultado = usuarioService.obtenerTodos();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Juan", resultado.get(0).getNombre());
        verify(usuarioRepository, times(1)).findAll();
    }

    @Test
    public void testObtenerPorId_CuandoExiste() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        Optional<UsuarioResponseDTO> resultado = usuarioService.obtenerPorId(1L);

        assertTrue(resultado.isPresent());
        assertEquals("Juan", resultado.get().getNombre());
    }

    @Test
    public void testObtenerPorId_CuandoNoExiste() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<UsuarioResponseDTO> resultado = usuarioService.obtenerPorId(99L);

        assertFalse(resultado.isPresent());
    }

    @Test
    public void testObtenerUsuarioConAuth_CuandoExisteYAuthExitoso() {
        AuthDTO authDTO = new AuthDTO();
        authDTO.setUsername("juanito");
        authDTO.setRol("CLIENTE");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(authClient.buscarPorEmail("juan@test.com")).thenReturn(Arrays.asList(authDTO));

        Optional<UsuarioConAuthDTO> resultado = usuarioService.obtenerUsuarioConAuth(1L);

        assertTrue(resultado.isPresent());
        assertEquals("Juan", resultado.get().getNombre());
        assertEquals("juanito", resultado.get().getUsername());
        assertEquals("CLIENTE", resultado.get().getRol());
    }

    @Test
    public void testObtenerUsuarioConAuth_CuandoNoTieneCuenta() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(authClient.buscarPorEmail("juan@test.com")).thenReturn(Collections.emptyList());

        Optional<UsuarioConAuthDTO> resultado = usuarioService.obtenerUsuarioConAuth(1L);

        assertTrue(resultado.isPresent());
        assertEquals("SIN_CUENTA", resultado.get().getUsername());
        assertEquals("SIN_ROL", resultado.get().getRol());
    }

    @Test
    public void testObtenerUsuarioConAuth_CuandoFallaAuthClient() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(authClient.buscarPorEmail("juan@test.com")).thenThrow(mock(FeignException.class));

        Optional<UsuarioConAuthDTO> resultado = usuarioService.obtenerUsuarioConAuth(1L);

        assertTrue(resultado.isPresent());
        assertEquals("ERROR_CONEXION", resultado.get().getUsername());
        assertEquals("ERROR_CONEXION", resultado.get().getRol());
    }

    @Test
    public void testGuardar() {
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        UsuarioResponseDTO resultado = usuarioService.guardar(usuarioRequestDTO);

        assertNotNull(resultado);
        assertEquals("Juan", resultado.getNombre());
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    public void testActualizar_CuandoExiste() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        Optional<UsuarioResponseDTO> resultado = usuarioService.actualizar(1L, usuarioRequestDTO);

        assertTrue(resultado.isPresent());
        assertEquals("Juan", resultado.get().getNombre());
    }

    @Test
    public void testActualizar_CuandoNoExiste() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<UsuarioResponseDTO> resultado = usuarioService.actualizar(99L, usuarioRequestDTO);

        assertFalse(resultado.isPresent());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    public void testEliminar() {
        doNothing().when(usuarioRepository).deleteById(1L);

        usuarioService.eliminar(1L);

        verify(usuarioRepository, times(1)).deleteById(1L);
    }
}
