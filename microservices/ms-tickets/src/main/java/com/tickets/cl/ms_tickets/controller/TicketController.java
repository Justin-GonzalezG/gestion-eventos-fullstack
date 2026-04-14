
// Recuerda compañero utilizar la URL para el Postman: http://localhost:8082/tickets

package com.tickets.cl.ms_tickets.controller;

import com.tickets.cl.ms_tickets.model.Ticket;
import com.tickets.cl.ms_tickets.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tickets")

public class TicketController {

    @Autowired
    private TicketService ticketService;

    @GetMapping
    public List<Ticket> listar() {
        return ticketService.obtenerTodos();
    }

    @PostMapping
    public Ticket crear(@RequestBody Ticket ticket) {
        return ticketService.guardar(ticket);
    }

    @GetMapping("/{id}")
    public Ticket buscar(@PathVariable Integer id) {
        return ticketService.obtenerPorId(id);
    }

    @DeleteMapping("/{id}")
    public void borrar(@PathVariable Integer id) {
        ticketService.eliminar(id);
    }
}
