package cl.eventos.ms_ordenes.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data

public class OrdenRequestDTO {

    @NotNull(message = "El ID del usuario es obligatorio.")
    private Long usuarioId;

    @NotEmpty(message = "La orden debe tener al menos un detalle.")
    private List<DetalleRequestDTO> detalles;
}
