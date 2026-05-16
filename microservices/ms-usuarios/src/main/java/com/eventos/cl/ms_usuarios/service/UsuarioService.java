package com.eventos.cl.ms_usuarios.service;

import com.eventos.cl.ms_usuarios.dto.UsuarioRequestDTO;
import com.eventos.cl.ms_usuarios.dto.UsuarioResponseDTO;
import com.eventos.cl.ms_usuarios.model.Usuario;
import com.eventos.cl.ms_usuarios.repository.UsuarioRepository;
import com.eventos.cl.ms_usuarios.client.AuthClient;
import com.eventos.cl.ms_usuarios.dto.AuthDTO;
import com.eventos.cl.ms_usuarios.dto.UsuarioConAuthDTO;
import feign.FeignException;
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
    private final AuthClient authClient;

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

    public Optional<UsuarioConAuthDTO> obtenerUsuarioConAuth(Long id) {
        return usuarioRepository.findById(id).map(usuario -> {
            UsuarioConAuthDTO dto = new UsuarioConAuthDTO();
            dto.setId(usuario.getId());
            dto.setRun(usuario.getRun());
            dto.setNombre(usuario.getNombre());
            dto.setApellido(usuario.getApellido());
            dto.setEmail(usuario.getEmail());
            dto.setTelefono(usuario.getTelefono());
            dto.setDireccion(usuario.getDireccion());

            try {
                // Buscamos las credenciales en ms-autenticacion usando el correo electrónico
                List<AuthDTO> credenciales = authClient.buscarPorEmail(usuario.getEmail());
                if (!credenciales.isEmpty()) {
                    AuthDTO auth = credenciales.get(0);
                    dto.setUsername(auth.getUsername());
                    dto.setRol(auth.getRol());
                } else {
                    dto.setUsername("SIN_CUENTA");
                    dto.setRol("SIN_ROL");
                }
            } catch (FeignException e) {
                // Si el ms-autenticacion está apagado o falla
                dto.setUsername("ERROR_CONEXION");
                dto.setRol("ERROR_CONEXION");
            }
            return dto;
        });
    }

    public UsuarioResponseDTO guardar(UsuarioRequestDTO dto) {
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
