package com.eventos.cl.ms_reportes.client;

import com.eventos.cl.ms_reportes.dto.OrdenDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "ms-ordenes", url = "${ms.ordenes.url}")
public interface OrdenClient {

    @GetMapping("/listar")
    List<OrdenDTO> listarOrdenes();
}
