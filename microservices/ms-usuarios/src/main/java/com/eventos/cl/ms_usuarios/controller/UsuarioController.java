package com.eventos.cl.ms_usuarios.controller;

import com.eventos.cl.ms_usuarios.model.Usuario;
import com.eventos.cl.ms_usuarios.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public List<Usuario> listar() {
        return usuarioService.obtenerTodos();
    }

    @PostMapping
    public Usuario crear(@RequestBody Usuario usuario) {
        return usuarioService.guardar(usuario);
    }

    @GetMapping("/{id}")
    public Usuario buscar(@PathVariable Integer id) {
        return usuarioService.obtenerPorId(id);
    }

    @DeleteMapping("/{id}")
    public void borrar(@PathVariable Integer id) {
        usuarioService.eliminar(id);
    }
}
