// http://localhost:8081/api/auth <-- Este es el URL Benja para el Atuenticador de Usuarios

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
            return "Usuario encontrado: " + usuario.getNombre() + " " + usuario.getApellido() + " Rol: " + usuario.getRol();
        } else {
            return "No existe ningún usuario con el ID " + id;
        }
    }

    @GetMapping("/filtrar/{rol}")
    public List<Usuario> filtrarPorRol(@PathVariable String rol) {
        return usuarioService.filtrarPorRol(rol);
    }

    @DeleteMapping("/{id}")
    public String borrar(@PathVariable Long id) {
        usuarioService.eliminar(id);
        return "El usuario con ID " + id + " ha sido eliminado con exito.";
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
