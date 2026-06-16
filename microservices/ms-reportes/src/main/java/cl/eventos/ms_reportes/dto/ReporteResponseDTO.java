package cl.eventos.ms_reportes.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReporteResponseDTO {

    private Long id;
    private Date fechaGeneracion;
    private String periodo;
    private BigDecimal ingresosTotales;
    private Integer totalTicketsVendidos;
    private String ticketMasPopular;
}
