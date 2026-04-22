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

    public List<Ticket> filtrarPorTipoYPrecio(String tipo, Integer precioMax) {
        return ticketRepository.buscarPorTipoYPrecio(tipo, precioMax);
    }

    public Ticket actualizar(Integer id, Ticket ticketActualizado) {
        Ticket ticketExistente = ticketRepository.findById(id).orElse(null);

        if (ticketExistente != null) {
            ticketExistente.setTipo(ticketActualizado.getTipo());
            ticketExistente.setPrecio(ticketActualizado.getPrecio());
            ticketExistente.setStock(ticketActualizado.getStock());

            return ticketRepository.save(ticketExistente);
        }
        return null;
    }
}
