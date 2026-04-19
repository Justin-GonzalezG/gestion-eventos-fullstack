package com.eventos.cl.ms_autenticacion.service;

import com.eventos.cl.ms_autenticacion.model.Usuario;
import com.eventos.cl.ms_autenticacion.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service

public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public Usuario registrarUsuario(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    public List<Usuario> obtenerTodos() {
        return usuarioRepository.findAll();
    }

    public Usuario obtenerPorId(Long id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    public void eliminar(Long id) {
        usuarioRepository.deleteById(id);
    }

    public Usuario login(String username, String password) {
        return usuarioRepository.findByUsername(username)
                .filter(u -> u.getPassword().equals(password))
                .orElse(null);
    }
    
    public List<Usuario> filtrarPorRol(String rol) {
        return usuarioRepository.findByRolDeUsuario(rol);
    }
}
