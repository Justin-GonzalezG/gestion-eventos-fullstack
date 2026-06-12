package cl.eventos.ms_checkin.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-ordenes")
public interface OrdenClient {

    @GetMapping("/api/ordenes/validar-pago/{ticketId}")
    boolean verificarPagoTicket(@PathVariable("ticketId") Long ticketId);
}
