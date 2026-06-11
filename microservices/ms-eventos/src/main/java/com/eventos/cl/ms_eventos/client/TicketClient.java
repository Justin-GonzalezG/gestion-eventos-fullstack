package cl.eventos.ms_eventos.client;

import cl.eventos.ms_eventos.dto.TicketDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "ms-tickets", url = "http://localhost:8084")
public interface TicketClient {

    @GetMapping("/api/tickets/categoria/{id}")
    List<TicketDTO> buscarPorCategoria(@PathVariable("id") Long id);
}
