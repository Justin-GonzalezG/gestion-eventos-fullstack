package cl.eventos.ms_ordenes.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data

public class DetalleRequestDTO {

    @NotNull(message = "El ID del ticket es obligatorio.")
    private Long ticketId;

    @NotNull(message = "La cantidad es obligatoria.")
    @Min(value = 1, message = "La cantidad mínima debe ser 1.")
    private Integer cantidad;

    @NotNull(message = "El precio unitario es obligatorio.")
    @Min(value = 0, message = "El precio no puede ser negativo.")
    private BigDecimal precioUnitario;
}
