package cl.eventos.ms_ordenes.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.Map;

@FeignClient(name = "ms-autenticacion", path = "/api/auth")
public interface AuthClient {

    @GetMapping("/validar")
    Map<String, Object> validarToken(@RequestParam("token") String token);
}
