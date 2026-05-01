package cl.eventos.ms_autenticacion.repository;

import cl.eventos.ms_autenticacion.model.Usuario;
import cl.eventos.ms_autenticacion.model.Rol; // Importante importar el Enum
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByUsername(String username);

    List<Usuario> findByRol(Rol rol);

    boolean existsByUsername(String username);
}