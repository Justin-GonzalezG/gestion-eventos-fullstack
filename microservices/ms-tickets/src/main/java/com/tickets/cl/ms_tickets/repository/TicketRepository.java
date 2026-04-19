package com.tickets.cl.ms_tickets.repository;

import com.tickets.cl.ms_tickets.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository

public interface TicketRepository extends JpaRepository<Ticket, Integer> {

    List<Ticket> findByTipo(String tipo);

    @Query("SELECT t from Ticket t where t.precio <= :precioMax")
    List<Ticket>buscarPorPrecioMaximo(@Param("precioMax") Integer precioMax);
}
