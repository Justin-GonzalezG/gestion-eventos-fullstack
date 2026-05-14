package cl.eventos.ms_pagos.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data

public class OrdenDTO {

    private Long id;
    private BigDecimal granTotal;
}
