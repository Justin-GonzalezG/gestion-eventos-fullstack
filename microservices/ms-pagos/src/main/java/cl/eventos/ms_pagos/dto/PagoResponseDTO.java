package cl.eventos.ms_pagos.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data

public class PagoResponseDTO {
    private Long id;
    private Long ordenId;
    private BigDecimal monto;
    private String metodoPago;
    private String estadoPago;
    private LocalDateTime fechaPago;
}