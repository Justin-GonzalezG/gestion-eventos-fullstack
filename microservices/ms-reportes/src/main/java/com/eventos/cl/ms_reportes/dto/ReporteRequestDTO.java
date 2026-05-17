package com.eventos.cl.ms_reportes.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReporteRequestDTO {

    @NotBlank(message = "El período no puede estar vacío (ej. Junio 2026)")
    private String periodo;

    private BigDecimal ingresosTotales;

    private Integer totalTicketsVendidos;

    private String ticketMasPopular;
}
