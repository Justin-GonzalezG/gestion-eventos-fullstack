package cl.eventos.ms_autenticacion.service;

import cl.eventos.ms_autenticacion.dto.UsuarioRegistroDTO;
import cl.eventos.ms_autenticacion.dto.UsuarioResponseDTO;
import cl.eventos.ms_autenticacion.model.Rol;
import cl.eventos.ms_autenticacion.model.Usuario;
import cl.eventos.ms_autenticacion.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.springframework.test.context.ActiveProfiles;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuario;
    private UsuarioRegistroDTO usuarioRegistroDTO;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("testuser");
        usuario.setPassword("encodedPassword");
        usuario.setEmail("test@test.com");
        usuario.setRol(Rol.CLIENTE);
        usuario.setActivo(true);

        usuarioRegistroDTO = new UsuarioRegistroDTO();
        usuarioRegistroDTO.setUsername("testuser");
        usuarioRegistroDTO.setPassword("rawPassword");
        usuarioRegistroDTO.setEmail("test@test.com");
        usuarioRegistroDTO.setRol(Rol.CLIENTE);
    }

    @Test
    void obtenerTodos_deberiaRetornarListaDeUsuarios() {
        when(usuarioRepository.findAll()).thenReturn(Arrays.asList(usuario));

        List<UsuarioResponseDTO> resultado = usuarioService.obtenerTodos();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("testuser", resultado.get(0).getUsername());
        verify(usuarioRepository, times(1)).findAll();
    }

    @Test
    void obtenerPorId_cuandoExiste_deberiaRetornarUsuario() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        Optional<UsuarioResponseDTO> resultado = usuarioService.obtenerPorId(1L);

        assertTrue(resultado.isPresent());
        assertEquals("testuser", resultado.get().getUsername());
        verify(usuarioRepository, times(1)).findById(1L);
    }

    @Test
    void obtenerPorId_cuandoNoExiste_deberiaRetornarVacio() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<UsuarioResponseDTO> resultado = usuarioService.obtenerPorId(99L);

        assertFalse(resultado.isPresent());
        verify(usuarioRepository, times(1)).findById(99L);
    }

    @Test
    void guardar_cuandoUsernameNoExiste_deberiaGuardarUsuario() {
        when(usuarioRepository.existsByUsername("testuser")).thenReturn(false);
        when(passwordEncoder.encode("rawPassword")).thenReturn("encodedPassword");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        UsuarioResponseDTO resultado = usuarioService.guardar(usuarioRegistroDTO);

        assertNotNull(resultado);
        assertEquals("testuser", resultado.getUsername());
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    void guardar_cuandoUsernameExiste_deberiaLanzarExcepcion() {
        when(usuarioRepository.existsByUsername("testuser")).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            usuarioService.guardar(usuarioRegistroDTO);
        });

        assertEquals("El nombre de usuario ya existe.", exception.getMessage());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void eliminar_cuandoExiste_deberiaEliminar() {
        when(usuarioRepository.existsById(1L)).thenReturn(true);

        usuarioService.eliminar(1L);

        verify(usuarioRepository, times(1)).deleteById(1L);
    }

    @Test
    void eliminar_cuandoNoExiste_deberiaLanzarExcepcion() {
        when(usuarioRepository.existsById(99L)).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            usuarioService.eliminar(99L);
        });

        assertEquals("Usuario no encontrado.", exception.getMessage());
        verify(usuarioRepository, never()).deleteById(anyLong());
    }

    @Test
    void buscarPorRol_deberiaRetornarUsuariosDelRol() {
        when(usuarioRepository.findByRol(Rol.CLIENTE)).thenReturn(Arrays.asList(usuario));

        List<UsuarioResponseDTO> resultado = usuarioService.buscarPorRol(Rol.CLIENTE);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(Rol.CLIENTE, resultado.get(0).getRol());
        verify(usuarioRepository, times(1)).findByRol(Rol.CLIENTE);
    }

    @Test
    void actualizarOptional_cuandoExiste_deberiaActualizar() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.encode("rawPassword")).thenReturn("encodedPassword");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        Optional<UsuarioResponseDTO> resultado = usuarioService.actualizarOptional(1L, usuarioRegistroDTO);

        assertTrue(resultado.isPresent());
        assertEquals("testuser", resultado.get().getUsername());
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    void actualizarOptional_cuandoNoExiste_deberiaRetornarVacio() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<UsuarioResponseDTO> resultado = usuarioService.actualizarOptional(99L, usuarioRegistroDTO);

        assertFalse(resultado.isPresent());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void buscarPorUsername_deberiaRetornarListaDeUsuarios() {
        when(usuarioRepository.findByUsernameContainingIgnoreCase("test")).thenReturn(Arrays.asList(usuario));

        List<UsuarioResponseDTO> resultado = usuarioService.buscarPorUsername("test");

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(usuarioRepository, times(1)).findByUsernameContainingIgnoreCase("test");
    }

    @Test
    void buscarPorUsernameParaAuth_deberiaRetornarEntidad() {
        when(usuarioRepository.findByUsername("testuser")).thenReturn(Optional.of(usuario));

        Optional<Usuario> resultado = usuarioService.buscarPorUsernameParaAuth("testuser");

        assertTrue(resultado.isPresent());
        assertEquals("testuser", resultado.get().getUsername());
        verify(usuarioRepository, times(1)).findByUsername("testuser");
    }
}
