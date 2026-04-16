package com.eventos.cl.ms_autenticacion.controller;

import com.eventos.cl.ms_autenticacion.model.Usuario;
import com.eventos.cl.ms_autenticacion.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/listar")
    public List<Usuario> listar() {
        return usuarioService.obtenerTodos();
    }

    @PostMapping("/registro")
    public String registrar(@RequestBody Usuario usuario) {
        usuarioService.registrarUsuario(usuario);
        return "¡Usuario " + usuario.getNombre() + " registrado con éxito!";
    }

    @GetMapping("/{id}")
    public String buscar(@PathVariable Long id) {
        Usuario usuario = usuarioService.obtenerPorId(id);

        if (usuario != null) {
            return "Usuario encontrado: " + usuario.getNombre() + " " + usuario.getApellido();
        } else {
            return "Error: No existe ningún usuario con el ID " + id;
        }
    }

    @DeleteMapping("/{id}")
    public String borrar(@PathVariable Long id) {
        usuarioService.eliminar(id);
        return "El usuario con ID " + id + " ha sido eliminado de la base de datos.";
    }

    @PostMapping("/login")
    public String login(@RequestBody Usuario loginData) {
        Usuario usuario = usuarioService.login(loginData.getUsername(), loginData.getPassword());

        if (usuario != null) {
            return "Login correcto. ¡Hola de nuevo, " + usuario.getNombre() + "!";
        } else {
            return "Login fallido: Usuario o contraseña incorrectos.";
        }
    }
}