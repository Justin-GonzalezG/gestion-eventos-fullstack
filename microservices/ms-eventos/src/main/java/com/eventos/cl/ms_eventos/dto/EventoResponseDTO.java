package com.eventos.cl.ms_eventos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;
import java.util.List;

// DTO de SALIDA
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventoResponseDTO {

    private Long id;
    private String nombre;
    private String informacionGeneral;
    private Date fechaHora;
    private String ubicacion;
    private Integer capacidadMaxima;
    private String estado;
    private List<TicketDTO> tickets;
}
