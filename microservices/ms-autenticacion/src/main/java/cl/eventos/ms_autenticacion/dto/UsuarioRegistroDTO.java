package cl.eventos.ms_autenticacion.dto;

import cl.eventos.ms_autenticacion.model.Rol;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class UsuarioRegistroDTO {

    @NotBlank(message = "El nombre de Usuario es obligatorio.")
    private String username;

    @NotBlank(message = "La Contraseña no puede estar vacía.")
    private String password;

    @NotBlank(message = "El Correo es obligatorio.")
    @Email(message = "El formato de Correo no es válido.")
    private String email;

    private Rol rol;
}
