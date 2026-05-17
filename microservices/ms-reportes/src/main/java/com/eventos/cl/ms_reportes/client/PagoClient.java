package com.eventos.cl.ms_reportes.client;

import com.eventos.cl.ms_reportes.dto.PagoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "ms-pagos", url = "${ms.pagos.url}")
public interface PagoClient {

    @GetMapping("/listar")
    List<PagoDTO> listarPagos();
}
