package cl.eventos.ms_reportes.client;

import cl.eventos.ms_reportes.dto.PagoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;

@FeignClient(name = "ms-pagos")
public interface PagoClient {

    @GetMapping("/api/pagos/listar")
    List<PagoDTO> listarPagos();
}
