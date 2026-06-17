package cl.eventos.ms_tickets.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class TicketRequestDTO {

    @NotBlank(message = "El tipo de Ticket no puede estar vacio.")
    private String tipo;

    @NotNull(message = "El precio es obligatorio.")
    @Positive(message = "El precio debe ser mayor a 0.")
    private BigDecimal precio;

    @NotNull(message = "El Stock es obligatorio.")
    @Positive(message = "El Stock debe ser mayor a 0.")
    private Integer stock;

    @NotNull(message = "La categoriaId debe ser obligatoria.")
    private Long categoriaId;
}
