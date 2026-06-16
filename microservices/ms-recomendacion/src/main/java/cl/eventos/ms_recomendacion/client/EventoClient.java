package cl.eventos.ms_recomendacion.client;

import cl.eventos.ms_recomendacion.dto.EventoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-eventos")
public interface EventoClient {

    @GetMapping("/api/eventos/{id}")
    ResponseEntity<EventoDTO> obtenerEventoPorId(@PathVariable("id") Long id);
}
