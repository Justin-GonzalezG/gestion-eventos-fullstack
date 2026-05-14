package cl.eventos.ms_pagos.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.Map;

@FeignClient(name = "ms-autenticacion", url = "http://localhost:8081/api/auth")
public interface AuthClient {

    @GetMapping("/validar")
    Map<String, Object> validarToken(@RequestParam("token") String token);
}
