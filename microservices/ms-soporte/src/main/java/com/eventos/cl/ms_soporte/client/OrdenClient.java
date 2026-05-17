package com.eventos.cl.ms_soporte.client;

import com.eventos.cl.ms_soporte.dto.OrdenDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-ordenes", url = "${ms.ordenes.url}")
public interface OrdenClient {

    @GetMapping("/{id}")
    OrdenDTO obtenerOrdenPorId(@PathVariable("id") Long id);
}
