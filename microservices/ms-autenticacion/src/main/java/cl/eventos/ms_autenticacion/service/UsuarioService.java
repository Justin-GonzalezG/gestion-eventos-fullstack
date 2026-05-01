package cl.eventos.ms_autenticacion.service;

import cl.eventos.ms_autenticacion.dto.UsuarioRegistroDTO;
import cl.eventos.ms_autenticacion.dto.UsuarioResponseDTO;
import cl.eventos.ms_autenticacion.model.Rol;
import cl.eventos.ms_autenticacion.model.Usuario;
import cl.eventos.ms_autenticacion.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor

public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

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
        return usuarioRepository.findAll()
                .stream()
                .map(this::mapearAResponse)
                .collect(Collectors.toList());
    }

    public Optional<UsuarioResponseDTO> obtenerPorId(Long id) {
        return usuarioRepository.findById(id).map(this::mapearAResponse);
    }

    public UsuarioResponseDTO guardar(UsuarioRegistroDTO dto) {
        if (usuarioRepository.existsByUsername(dto.getUsername())) {
            throw new RuntimeException("El nombre de usuario ya existe.");
        }

        Usuario usuario = new Usuario();
        usuario.setUsername(dto.getUsername());
        usuario.setPassword(dto.getPassword());
        usuario.setEmail(dto.getEmail());
        usuario.setRol(dto.getRol());

        Usuario guardado = usuarioRepository.save(usuario);
        return mapearAResponse(guardado);
    }

    public void eliminar(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RuntimeException("Usuario no encontrado.");
        }
        usuarioRepository.deleteById(id);
    }

    public List<UsuarioResponseDTO> buscarPorRol(Rol rol) {
        return usuarioRepository.findByRol(rol)
                .stream()
                .map(this::mapearAResponse)
                .collect(Collectors.toList());
    }
}