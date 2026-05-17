package com.eventos.cl.ms_soporte.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketConDetalleDTO {
    private Long id;
    private Long usuarioId;
    private Long ordenId;
    private String asunto;
    private String descripcionProblema;
    private String estado;
    private Date fechaCreacion;
    
    // Objetos anidados
    private UsuarioDTO detalleUsuario;
    private OrdenDTO detalleOrden;
}
