package com.eventos.cl.ms_recomendacion.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

// Este DTO es un "Espejo". Sirve para atrapar y leer el JSON que nos envía el ms-eventos.
// Debe tener exactamente las mismas variables que el EventoResponseDTO del otro microservicio.
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventoDTO {
    private Long id;
    private String nombre;
    private String informacionGeneral;
    private Date fechaHora;
    private String ubicacion;
    private Integer capacidadMaxima;
    private String estado;
}
