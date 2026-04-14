
// Recuerda utilizar la URL para el Postman: http://localhost:8082/tickets

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

    // 1. Obtener todos los Tickets
    // Con esto le pediremos al sistema que muestre una lista con todos los tickest que estan
    // guardado en la Base de Datos.
    @GetMapping
    public List<Ticket> listar() {
        return ticketService.obtenerTodos();
    }

    // 2. Agregamos un nuevo ticket a la base de datos.
    @PostMapping
    public Ticket crear(@RequestBody Ticket ticket) {
        return ticketService.guardar(ticket);
    }

    // 3. Busca un solo ticket usando la ID del ticket.
    @GetMapping("/{id}")
    public Ticket buscar(@PathVariable Integer id) {
        return ticketService.obtenerPorId(id);
    }

    // 4. Borrarmos un ticket del sistema permanentemente.
    @DeleteMapping("/{id}")
    public void borrar(@PathVariable Integer id) {
        ticketService.eliminar(id);
    }
}