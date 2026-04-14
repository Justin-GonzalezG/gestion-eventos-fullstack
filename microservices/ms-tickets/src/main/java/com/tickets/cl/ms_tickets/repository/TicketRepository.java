package com.tickets.cl.ms_tickets.repository;


import com.fasterxml.jackson.annotation.JacksonAnnotation;
import com.tickets.cl.ms_tickets.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface TicketRepository extends JpaRepository<Ticket, Integer> {
}
