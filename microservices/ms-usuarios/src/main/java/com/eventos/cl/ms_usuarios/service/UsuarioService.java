package com.eventos.cl.ms_usuarios.service;

import com.eventos.cl.ms_usuarios.dto.UsuarioRequestDTO;
import com.eventos.cl.ms_usuarios.dto.UsuarioResponseDTO;
import com.eventos.cl.ms_usuarios.model.Usuario;
import com.eventos.cl.ms_usuarios.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor // Nueva regla: Inyecta dependencias automaticamente
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    // Transformador manual: Entidad / DTO (Salida)
    private UsuarioResponseDTO mapearAResponse(Usuario usuario) {
        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getRun(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getEmail(),
                usuario.getTelefono(),
                usuario.getDireccion()
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

    public UsuarioResponseDTO guardar(UsuarioRequestDTO dto) {
        // Transformar DTO (Entrada) / Entidad
        Usuario usuario = new Usuario();
        usuario.setRun(dto.getRun());
        usuario.setNombre(dto.getNombre());
        usuario.setApellido(dto.getApellido());
        usuario.setEmail(dto.getEmail());
        usuario.setTelefono(dto.getTelefono());
        usuario.setDireccion(dto.getDireccion());

        Usuario guardado = usuarioRepository.save(usuario);
        return mapearAResponse(guardado);
    }

    public Optional<UsuarioResponseDTO> actualizar(Long id, UsuarioRequestDTO dto) {
        return usuarioRepository.findById(id).map(usuario -> {
            usuario.setRun(dto.getRun());
            usuario.setNombre(dto.getNombre());
            usuario.setApellido(dto.getApellido());
            usuario.setEmail(dto.getEmail());
            usuario.setTelefono(dto.getTelefono());
            usuario.setDireccion(dto.getDireccion());
            
            Usuario actualizado = usuarioRepository.save(usuario);
            return mapearAResponse(actualizado);
        });
    }

    public void eliminar(Long id) {
        usuarioRepository.deleteById(id);
    }
}
