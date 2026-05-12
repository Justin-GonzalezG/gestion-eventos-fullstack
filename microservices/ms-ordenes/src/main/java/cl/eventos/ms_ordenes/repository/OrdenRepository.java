package cl.eventos.ms_ordenes.repository;

import cl.eventos.ms_ordenes.model.Orden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrdenRepository extends JpaRepository<Orden, Long> {

    List<Orden> findByUsuarioId(Long usuarioId);

    boolean existsByEstadoAndDetalles_TicketId(String estado, Long ticketId);
}
