package cl.eventos.ms_usuarios.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Este es el DTO Espejo para atrapar la respuesta de ms-autenticacion
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthDTO {
    private String username;
    private String email;
    private String rol;
}
