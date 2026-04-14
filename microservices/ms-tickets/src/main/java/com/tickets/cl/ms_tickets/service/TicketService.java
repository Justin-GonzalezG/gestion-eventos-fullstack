
package com.tickets.cl.ms_tickets.service;

import com.tickets.cl.ms_tickets.model.Ticket;
import com.tickets.cl.ms_tickets.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service

public class TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    public List<Ticket> obtenerTodos() {
        return ticketRepository.findAll();
    }

    public Ticket guardar(Ticket ticket) {
        return ticketRepository.save(ticket);
    }

    public Ticket obtenerPorId(Integer id) {
        return ticketRepository.findById(id).orElse(null);
    }

    public void eliminar(Integer id){
        ticketRepository.deleteById(id);
    }
}
