package com.eventos.cl.ms_recomendacion.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecomendacionResponseDTO {

    private Long id;
    private Long usuarioId;
    private Long eventoSugeridoId;
    private String motivo;
    private Integer nivelAfinidad;
    private Date fechaRecomendacion;
}
