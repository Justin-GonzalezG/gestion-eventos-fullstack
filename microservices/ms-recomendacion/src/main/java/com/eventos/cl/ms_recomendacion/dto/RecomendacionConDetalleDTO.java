package com.eventos.cl.ms_recomendacion.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Este es el "Súper DTO" que le entregaremos al usuario final en Postman.
// Contiene las variables propias de la Recomendación y además tiene un objeto anidado o junto (EventoDTO) que trajimos por red.
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecomendacionConDetalleDTO {

    // Datos propios de la recomendacion
    private Long recomendacionId;
    private Long usuarioId;
    private String motivo;
    private Integer nivelAfinidad;
    private String fechaRecomendacion; // En String para formato limpio si se requiere

    // Objeto anidado traido via Feign
    private EventoDTO detalleEvento;
}
