package com.eventos.cl.ms_reportes.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class PagoDTO {
    private Long id;
    private BigDecimal monto;
    private String estadoPago;
}
