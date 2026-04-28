package com.eventos.cl.ms_usuarios.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Datos que SALEN hacia Postman (No lleva validaciones)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResponseDTO {

    private Long id;
    private String run;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    private String direccion;
}
