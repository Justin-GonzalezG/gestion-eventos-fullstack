package com.eventos.cl.ms_recomendacion.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDTO {
    private String run;
    private String nombre;
    private String apellido;
    private String email;
}
