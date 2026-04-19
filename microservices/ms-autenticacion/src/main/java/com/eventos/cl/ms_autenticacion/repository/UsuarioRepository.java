package com.eventos.cl.ms_autenticacion.repository;

import com.eventos.cl.ms_autenticacion.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByUsername(String username);

    @Query("SELECT u FROM Usuario u WHERE UPPER(u.rol) = UPPER(:rol)")
    List<Usuario> findByRolDeUsuario(@Param("rol") String rol);
}
