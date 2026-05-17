package com.eventos.cl.ms_recomendacion.client;

import com.eventos.cl.ms_recomendacion.dto.UsuarioDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-usuarios", url = "${ms.usuarios.url}")
public interface UsuarioClient {

    @GetMapping("/{id}")
    ResponseEntity<UsuarioDTO> obtenerUsuarioPorId(@PathVariable("id") Long id);
}
