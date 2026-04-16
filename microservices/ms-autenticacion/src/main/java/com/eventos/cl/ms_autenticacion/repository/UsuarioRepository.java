package com.eventos.cl.ms_autenticacion.repository;

import com.eventos.cl.ms_autenticacion.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Ahora con esta funcion buscamos a un usuario por su nombre para que se login.
    Optional<Usuario> findByUsername(String username);
}
