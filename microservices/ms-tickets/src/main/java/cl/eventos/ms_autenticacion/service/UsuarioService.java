package cl.eventos.ms_autenticacion.service;

import cl.eventos.ms_autenticacion.dto.UsuarioRegistroDTO;
import cl.eventos.ms_autenticacion.dto.UsuarioResponseDTO;
import cl.eventos.ms_autenticacion.model.Rol;
import cl.eventos.ms_autenticacion.model.Usuario;
import cl.eventos.ms_autenticacion.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j

public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    private UsuarioResponseDTO mapearAResponse(Usuario usuario) {
        return new UsuarioResponseDTO(

                usuario.getId(),
                usuario.getUsername(),
                usuario.getEmail(),
                usuario.getRol(),
                usuario.isActivo()
        );
    }

    public List<UsuarioResponseDTO> obtenerTodos() {
        log.info("Obteniendo lista completa de usuarios");

        return usuarioRepository.findAll()
                .stream()
                .map(this::mapearAResponse)
                .collect(Collectors.toList());
    }

    public Optional<UsuarioResponseDTO> obtenerPorId(Long id) {
        log.info("Buscando usuario con ID: {}", id);

        return usuarioRepository.findById(id).map(this::mapearAResponse);
    }

    public UsuarioResponseDTO guardar(UsuarioRegistroDTO dto) {
        log.info("Iniciando proceso de registro para usuario: {}", dto.getUsername());

        if (usuarioRepository.existsByUsername(dto.getUsername())) {

            log.warn("Fallo al registrar: El nombre de usuario {} ya existe", dto.getUsername());
            throw new RuntimeException("El nombre de usuario ya existe.");
        }

        Usuario usuario = new Usuario();
        usuario.setUsername(dto.getUsername());

        String passwordHaseada = passwordEncoder.encode(dto.getPassword());
        usuario.setPassword(passwordHaseada);
        log.debug("Contraseña cifrada con éxito para {}", dto.getUsername());

        usuario.setEmail(dto.getEmail());
        usuario.setRol(dto.getRol());

        Usuario guardado = usuarioRepository.save(usuario);
        log.info("Usuario registrado exitosamente. ID generado: {}", guardado.getId());

        return mapearAResponse(guardado);
    }

    public void eliminar(Long id) {
        log.info("Solicitud para eliminar usuario ID: {}", id);

        if (!usuarioRepository.existsById(id)) {

            log.error("Error al eliminar: Usuario ID {} no encontrado", id);
            throw new RuntimeException("Usuario no encontrado.");
        }
        usuarioRepository.deleteById(id);
    }

    public List<UsuarioResponseDTO> buscarPorRol(Rol rol) {
        log.info("Filtrando usuarios por rol: {}", rol);

        return usuarioRepository.findByRol(rol)
                .stream()
                .map(this::mapearAResponse)
                .collect(Collectors.toList());
    }

    public UsuarioResponseDTO actualizar(Long id, UsuarioRegistroDTO dto) {
        log.info("Actualizando datos del usuario ID: {}", id);

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("No se puede actualizar: ID {} no existe", id);
                    return new RuntimeException("Usuario con ID " + id + " no encontrado.");
                });

        usuario.setUsername(dto.getUsername());
        usuario.setPassword(passwordEncoder.encode(dto.getPassword()));

        usuario.setEmail(dto.getEmail());
        usuario.setRol(dto.getRol());

        Usuario actualizado = usuarioRepository.save(usuario);
        log.info("Usuario ID {} actualizado correctamente", id);

        return mapearAResponse(actualizado);
    }

    public List<UsuarioResponseDTO> buscarPorUsername(String username) {
        log.info("Buscando usuarios que coincidan con: {}", username);

        return usuarioRepository.findByUsernameContainingIgnoreCase(username)
                .stream()
                .map(this::mapearAResponse)
                .collect(Collectors.toList());
    }

    public Optional<Usuario> buscarPorUsernameParaAuth(String username) {
        log.info("Buscando entidad completa para login: {}", username);

        return usuarioRepository.findByUsername(username);
    }
}
