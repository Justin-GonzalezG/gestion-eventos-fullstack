package cl.eventos.ms_reportes.client;

import cl.eventos.ms_reportes.dto.TicketDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-tickets")
public interface TicketClient {

    @GetMapping("/api/tickets/{id}")
    TicketDTO obtenerTicketPorId(@PathVariable("id") Long id);
}
