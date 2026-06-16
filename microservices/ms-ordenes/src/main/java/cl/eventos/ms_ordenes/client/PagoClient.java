package cl.eventos.ms_ordenes.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-pagos", path = "/api/pagos")
public interface PagoClient {

    @GetMapping("/buscar-por-orden/{ordenId}")
    Object buscarPagoPorOrdenId(@PathVariable("ordenId") Long ordenId);
}
