package cl.eventos.ms_autenticacion.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data

public class LoginRequestDTO {

    @NotBlank(message = "Debes ingresar tu nombre de usuario")
    private String username;

    @NotBlank(message = "Debes ingresar tu contraseña")
    private String password;
}
