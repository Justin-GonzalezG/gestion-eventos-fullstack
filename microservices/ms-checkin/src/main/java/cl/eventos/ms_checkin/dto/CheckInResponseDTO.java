package cl.eventos.ms_checkin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class CheckInResponseDTO {

    private Long id;
    private Long ticketId;
    private LocalDateTime fechaIngreso;

}
