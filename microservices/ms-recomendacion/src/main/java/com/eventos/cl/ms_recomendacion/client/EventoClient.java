package com.eventos.cl.ms_recomendacion.client;

import com.eventos.cl.ms_recomendacion.dto.EventoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// Esta interfaz actúa como una "antena" que apunta a otro microservicio.
// Usamos @FeignClient para decirle a Spring Boot a qué URL debe conectarse.
// En este caso, apunta a la URL configurada en el application.properties
@FeignClient(name = "ms-eventos", url = "${ms.eventos.url}")
public interface EventoClient {

    // Este metodo es una copia exacta del controlador del ms-eventos.
    // Al llamarlo, Feign hará una petición GET invisible a la URL de arriba.
    @GetMapping("/{id}")
    ResponseEntity<EventoDTO> obtenerEventoPorId(@PathVariable("id") Long id);
}
