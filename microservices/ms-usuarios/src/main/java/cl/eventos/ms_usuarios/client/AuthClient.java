package cl.eventos.ms_usuarios.client;

import cl.eventos.ms_usuarios.dto.AuthDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "ms-autenticacion")
public interface AuthClient {

    @GetMapping("/api/auth/usuario/buscar")
    List<AuthDTO> buscarPorEmail(@RequestParam("username") String username);
}
