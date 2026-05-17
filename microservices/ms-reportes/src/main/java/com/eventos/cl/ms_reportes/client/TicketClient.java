package com.eventos.cl.ms_reportes.client;

import com.eventos.cl.ms_reportes.dto.TicketDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-tickets", url = "${ms.tickets.url}")
public interface TicketClient {

    @GetMapping("/{id}")
    TicketDTO obtenerTicketPorId(@PathVariable("id") Long id);
}
