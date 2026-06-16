package cl.eventos.ms_reportes.client;

import cl.eventos.ms_reportes.dto.OrdenDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;

@FeignClient(name = "ms-ordenes")
public interface OrdenClient {

    @GetMapping("/api/ordenes/listar")
    List<OrdenDTO> listarOrdenes();
}
