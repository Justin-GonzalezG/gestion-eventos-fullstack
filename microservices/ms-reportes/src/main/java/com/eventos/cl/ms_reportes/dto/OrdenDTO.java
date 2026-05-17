package com.eventos.cl.ms_reportes.dto;

import lombok.Data;
import java.util.List;

@Data
public class OrdenDTO {
    private Long id;
    private List<DetalleOrdenDTO> detalles;
}
