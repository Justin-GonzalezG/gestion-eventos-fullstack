package cl.eventos.ms_checkin.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-tickets")
public interface TicketClient {

    @GetMapping("/api/tickets/{id}")
    ResponseEntity<?> validarTicket(@PathVariable("id") Long id);
}
