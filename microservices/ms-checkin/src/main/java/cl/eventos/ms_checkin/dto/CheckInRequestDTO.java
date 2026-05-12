package cl.eventos.ms_checkin.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class CheckInRequestDTO {

    @NotNull(message = "El ID del ticket es obligatorio para validar el acceso.")
    private Long ticketId;
}
