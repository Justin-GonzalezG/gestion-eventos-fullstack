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

    @NotNull(message = "Debes indicar los ingresos totales")
    @PositiveOrZero(message = "Los ingresos no pueden ser negativos")
    private BigDecimal ingresosTotales;

    @NotNull(message = "Debes indicar el total de tickets vendidos")
    @PositiveOrZero(message = "El total de tickets vendidos no puede ser negativo")
    private Integer totalTicketsVendidos;

    @NotBlank(message = "Debes indicar el ticket más popular")
    private String ticketMasPopular;
}
