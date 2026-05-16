package com.eventos.cl.ms_usuarios.client;

import com.eventos.cl.ms_usuarios.dto.AuthDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "ms-autenticacion", url = "${ms.autenticacion.url}")
public interface AuthClient {

    // Llama al endpoint: /api/auth/usuario/buscar?username={email}
    // Ojo: Retorna una lista porque el ms-autenticacion devuelve List<UsuarioResponseDTO>
    @GetMapping("/usuario/buscar")
    List<AuthDTO> buscarPorEmail(@RequestParam("username") String email);
}
