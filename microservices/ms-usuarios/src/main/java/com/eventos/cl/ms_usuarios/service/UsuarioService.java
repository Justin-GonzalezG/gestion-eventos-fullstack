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

    // Obtener la lista de todos los usuarios.
    public List<Usuario> obtenerTodos() {
        return usuarioRepository.findAll();
    }

    // Agregar un nuevo usuario a la base de datos.
    public Usuario guardar(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    // Busca un usuario por ID.
    public Usuario obtenerPorId(Integer id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    // Eliminar un usuario de forma permanente.
    public void eliminar(Integer id){
        usuarioRepository.deleteById(id);
    }
}
