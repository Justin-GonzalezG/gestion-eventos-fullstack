package com.eventos.cl.ms_soporte.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tickets_soporte")
public class TicketSoporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long usuarioId;

    // Puede ser nulo porque un ticket puede no estar asociado a una orden
    @Column(nullable = true)
    private Long ordenId;

    @Column(nullable = false, length = 150)
    private String asunto;

    @Column(nullable = false, length = 1000)
    private String descripcionProblema;

    @Column(nullable = false, length = 50)
    private String estado;

    @Column(nullable = false)
    private Date fechaCreacion;
}
