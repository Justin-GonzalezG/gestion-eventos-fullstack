package cl.eventos.ms_pagos.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(name = "ms-ordenes", url = "${config.ms-ordenes.url}")
public interface OrdenClient {

    @PutMapping("/actualizar/{id}")
    void actualizar(@PathVariable("id") Long id, @RequestParam("nuevoEstado") String nuevoEstado);
}
