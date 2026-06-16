package cl.eventos.ms_checkin.repository;

import cl.eventos.ms_checkin.model.CheckIn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository

public interface CheckInRepository extends JpaRepository<CheckIn, Long> {

    boolean existsByTicketId(Long ticketId);

    @Query("SELECT c FROM CheckIn c WHERE c.ticketId = :ticketId")
    Optional<CheckIn> buscarPorTicketId(@Param("ticketId") Long ticketId);

    @Query("SELECT COUNT(c) FROM CheckIn c")
    Long contarTotalIngresos();
}
