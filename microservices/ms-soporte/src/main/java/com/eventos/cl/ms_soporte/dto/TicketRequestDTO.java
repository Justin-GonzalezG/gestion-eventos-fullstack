package cl.eventos.ms_soporte.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketRequestDTO {

    @NotNull(message = "Debes proveer el ID del usuario afectado")
    private Long usuarioId;

    // No ponemos validaciones a ordenId porque es opcional
    private Long ordenId;

    @NotBlank(message = "El asunto del problema es obligatorio")
    private String asunto;

    @NotBlank(message = "Por favor, describe en detalle tu problema")
    private String descripcionProblema;
}
