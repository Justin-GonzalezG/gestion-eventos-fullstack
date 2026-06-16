package cl.eventos.ms_eventos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

// DTO de ENTRADA (con validaciones)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventoRequestDTO {

    @NotBlank(message = "El nombre del evento no puede estar vacío")
    private String nombre;

    @NotBlank(message = "Debes proveer la información general del evento")
    private String informacionGeneral;

    @NotNull(message = "La fecha y hora son obligatorias")
    private Date fechaHora;

    @NotBlank(message = "La ubicación no puede estar vacía")
    private String ubicacion;

    @NotNull(message = "Debes indicar la capacidad máxima")
    @Positive(message = "La capacidad máxima debe ser mayor a 0")
    private Integer capacidadMaxima;

    @NotBlank(message = "Debes asignar un estado inicial (Ej: Activo, Cancelado)")
    private String estado;
}
