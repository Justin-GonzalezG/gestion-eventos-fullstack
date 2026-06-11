package cl.eventos.ms_soporte.client;

import cl.eventos.ms_soporte.dto.OrdenDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-ordenes")
public interface OrdenClient {

    @GetMapping("/api/ordenes/{id}")
    OrdenDTO obtenerOrdenPorId(@PathVariable("id") Long id);
}
