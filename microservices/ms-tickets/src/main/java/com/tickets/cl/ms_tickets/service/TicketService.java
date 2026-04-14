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

    // 1.
    // Con esto vamos a obtener la lista de todos los Tickets, es decir mostraremos al usuario los tickets disponibles
    // Me base en la Guia de los libros del Duoc (Bibliotecaduoc).
    public List<Ticket> obtenerTodos() {
        return ticketRepository.findAll();
    }

    //2.
    // Esto deberia guardar los tickets. Agrega un ticket nuevo a la base de datos.
    public Ticket guardar(Ticket ticket) {
        return ticketRepository.save(ticket);
    }

    // 3.
    // Esto deberia buscar por ID los Tickets de los Usuarios. Busca un solo ticket.
    public Ticket obtenerPorId(Integer id) {
        return ticketRepository.findById(id).orElse(null);
    }

    // 4.
    // Y este metodo nos deberia Eliminar el Ticket del Usuario. Borra permanente.
    public void eliminar(Integer id){
        ticketRepository.deleteById(id);
    }
}
