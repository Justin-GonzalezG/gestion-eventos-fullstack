package cl.eventos.ms_recomendacion.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecomendacionRequestDTO {

    @NotNull(message = "El ID del usuario es obligatorio para hacer la recomendación")
    private Long usuarioId;

    @NotNull(message = "El ID del evento sugerido es obligatorio")
    private Long eventoSugeridoId;

    @NotBlank(message = "Debes proveer un motivo de sugerencia")
    private String motivo;

    @NotNull(message = "El nivel de afinidad es obligatorio")
    @Min(value = 1, message = "La afinidad mínima es 1%")
    @Max(value = 100, message = "La afinidad máxima es 100%")
    private Integer nivelAfinidad;
}
