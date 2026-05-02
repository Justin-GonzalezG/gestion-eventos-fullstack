package cl.eventos.ms_pagos.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data

public class PagoRequestDTO {
    private Long ordenId;
    private BigDecimal monto;
    private String metodoPago;
}