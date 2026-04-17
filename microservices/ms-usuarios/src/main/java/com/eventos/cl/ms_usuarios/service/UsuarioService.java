package com.eventos.cl.ms_usuarios.service;

import com.eventos.cl.ms_usuarios.model.Usuario;
import com.eventos.cl.ms_usuarios.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<Usuario> obtenerTodos() {
        return usuarioRepository.findAll();
    }

    public Usuario guardar(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    public Usuario obtenerPorId(Integer id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    public void eliminar(Integer id){
        usuarioRepository.deleteById(id);
    }
}
