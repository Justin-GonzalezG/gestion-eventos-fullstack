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

    // Esta es la Funcion que cree para Registrar el Usuriario.
    public Usuario registrarUsuario(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    // Listamos los Usuarios.
    public List<Usuario> obtenerTodos() {
        return usuarioRepository.findAll();
    }

    public Usuario obtenerPorId(Long id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    public void eliminar(Long id) {
        usuarioRepository.deleteById(id);
    }

    // Función para validar el ingreso (Login)
    public Usuario login(String username, String password) {
        return usuarioRepository.findByUsername(username)
                .filter(u -> u.getPassword().equals(password))
                .orElse(null);
    }
}