package com.tickets.cl.ms_tickets.repository;

import com.tickets.cl.ms_tickets.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository

public interface TicketRepository extends JpaRepository<Ticket, Integer> {

    List<Ticket> findByTipo(String tipo);

    @Query("SELECT ticket FROM Ticket ticket WHERE ticket.tipo = :tipo AND ticket.precio <= :precioMax")
    List<Ticket> buscarPorTipoYPrecio(@Param("tipo") String tipo, @Param("precioMax") Integer precioMax);
}
