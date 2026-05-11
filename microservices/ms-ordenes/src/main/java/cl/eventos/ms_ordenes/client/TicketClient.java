package cl.eventos.ms_ordenes.client;

import cl.eventos.ms_ordenes.dto.TicketDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "ms-tickets", url = "${config.ms-tickets.url}")

public interface TicketClient {

    @GetMapping("/{id}")
    TicketDTO obtenerTicketPorId(@PathVariable("id") Long id);

    @PutMapping("/{id}/stock")
    public void actualizarStock(@PathVariable("id") Long id, @RequestParam("nuevoStock") Integer nuevoStock);
}
