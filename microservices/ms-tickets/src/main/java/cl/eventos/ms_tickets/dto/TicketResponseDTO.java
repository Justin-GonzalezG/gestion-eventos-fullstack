package cl.eventos.ms_tickets.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketResponseDTO {

    private Long id;
    private String tipo;
    private BigDecimal precio;
    private Integer stock;
    private String categoriaNombre;
}