
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
    public String crear(@RequestBody Ticket ticket) {
        ticketService.guardar(ticket);
        return "El ticket de tipo '" + ticket.getTipo() + "' se guardo con excito .";
    }

    @GetMapping("/filtrar/{tipo}/{precioMax}")
    public List<Ticket> filtrar(@PathVariable String tipo, @PathVariable Integer precioMax) {
        return ticketService.filtrarPorTipoYPrecio(tipo, precioMax);
    }

    @GetMapping("/{id}")
    public Ticket buscar(@PathVariable Integer id) {
        return ticketService.obtenerPorId(id);
    }

    @DeleteMapping("/{id}")
    public String borrar(@PathVariable Integer id) {
        Ticket ticket = ticketService.obtenerPorId(id);

        if (ticket != null) {
            ticketService.eliminar(id);
            return "El ticket con ID " + id + " ha sido eliminado con exito.";
        } else {
            return "No se pudo eliminar, debido que el ticket no existe.";
        }
    }
}
