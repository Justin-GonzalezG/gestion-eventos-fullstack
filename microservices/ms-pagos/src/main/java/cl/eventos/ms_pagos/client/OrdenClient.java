package cl.eventos.ms_pagos.client;

import cl.eventos.ms_pagos.dto.OrdenDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "ms-ordenes", url = "http://localhost:8085/api/ordenes")
public interface OrdenClient {

    @GetMapping("/{id}")
    OrdenDTO buscarPorId(@PathVariable("id") Long id);

    @PutMapping("/actualizar/{id}")
    void actualizar(@PathVariable("id") Long id, @RequestParam("nuevoEstado") String nuevoEstado);
}
