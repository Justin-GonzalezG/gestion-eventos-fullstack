package com.eventos.cl.ms_recomendacion.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "recomendaciones")
public class Recomendacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long usuarioId;

    @Column(nullable = false)
    private Long eventoSugeridoId;

    @Column(nullable = false, length = 250)
    private String motivo;

    @Column(nullable = false)
    private Integer nivelAfinidad;

    @Column(nullable = false)
    private Date fechaRecomendacion;
}
