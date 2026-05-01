package cl.eventos.ms_ordenes.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data

public class TicketDTO {
    private Long id;
    private String nombre;
    private BigDecimal precio;
    private Integer stockActual;
}