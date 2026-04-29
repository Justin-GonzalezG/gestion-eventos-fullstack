package com.eventos.cl.ms_autenticacion.dto;

import com.eventos.cl.ms_autenticacion.model.Rol;
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

    @NotBlank(message = "La Contraseña no puede estar vacia.")
    private String password;

    @NotBlank(message = "El Correo es obligatorio.")
    @Email(message = "El formato Correo no es valido.")
    private String correo;

    private Rol rol;
}