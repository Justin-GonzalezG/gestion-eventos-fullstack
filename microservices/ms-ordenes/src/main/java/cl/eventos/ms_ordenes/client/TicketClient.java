package cl.eventos.ms_ordenes.client;

import cl.eventos.ms_ordenes.dto.TicketDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "ms-tickets")
public interface TicketClient {

    @GetMapping("/api/tickets/{id}")
    TicketDTO obtenerTicketPorId(@PathVariable("id") Long id);

    @PutMapping("/api/tickets/{id}/stock")
    void actualizarStock(@PathVariable("id") Long id, @RequestParam("nuevoStock") Integer nuevoStock);
}
