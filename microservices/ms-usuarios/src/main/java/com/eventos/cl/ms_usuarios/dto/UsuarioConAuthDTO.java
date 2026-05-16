package com.eventos.cl.ms_usuarios.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Súper DTO que combina el perfil de usuario con sus datos de autenticación
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioConAuthDTO {
    private Long id;
    private String run;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    private String direccion;
    
    // Datos traídos desde ms-autenticacion
    private String username;
    private String rol;
}
