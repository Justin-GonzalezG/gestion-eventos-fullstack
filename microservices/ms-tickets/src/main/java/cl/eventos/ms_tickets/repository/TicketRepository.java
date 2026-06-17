package cl.eventos.ms_tickets.repository;

import cl.eventos.ms_tickets.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    @Query("SELECT t FROM Ticket t WHERE t.categoria.id = :categoriaId")
    List<Ticket> findByCategoriaId(@Param("categoriaId") Long categoriaId);

    @Query("SELECT t FROM Ticket t WHERE t.precio <= :precioMax ORDER BY t.precio DESC")
    List<Ticket> findTicketsBajoPresupuesto(@Param("precioMax") BigDecimal precioMax);

    List<Ticket> findByTipoContainingIgnoreCase(String tipo);
}
