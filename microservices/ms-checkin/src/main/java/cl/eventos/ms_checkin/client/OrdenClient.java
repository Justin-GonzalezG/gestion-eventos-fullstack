package cl.eventos.ms_checkin.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-ordenes", url = "${config.ms-ordenes.url}")
public interface OrdenClient {

    @GetMapping("/validar-pago/{ticketId}")
    boolean verificarPagoTicket(@PathVariable("ticketId") Long ticketId);
    
}
