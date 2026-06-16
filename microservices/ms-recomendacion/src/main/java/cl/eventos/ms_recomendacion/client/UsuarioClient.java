package cl.eventos.ms_recomendacion.client;

import cl.eventos.ms_recomendacion.dto.UsuarioDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-usuarios")
public interface UsuarioClient {

    @GetMapping("/api/usuarios/{id}")
    ResponseEntity<UsuarioDTO> obtenerUsuarioPorId(@PathVariable("id") Long id);
}
