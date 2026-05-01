package cl.eventos.ms_autenticacion.dto;

import cl.eventos.ms_autenticacion.model.Rol;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class UsuarioResponseDTO {

    private Long id;
    private String username;
    private String email;
    private Rol rol;
    private boolean activo;
}