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

    // 1. Obtener todos los Usuarios,
    @GetMapping
    public List<Usuario> listar() {
        return usuarioService.obtenerTodos();
    }

    // 2. Agregamos un nuevo usuario a la base de datos.
    @PostMapping
    public Usuario crear(@RequestBody Usuario usuario) {
        return usuarioService.guardar(usuario);
    }

    // 3. Busca un usuario usando el ID.
    @GetMapping("/{id}")
    public Usuario buscar(@PathVariable Integer id) {
        return usuarioService.obtenerPorId(id);
    }

    // 4. Borramos un usuario permanentemente.
    @DeleteMapping("/{id}")
    public void borrar(@PathVariable Integer id) {
        usuarioService.eliminar(id);
    }
}
