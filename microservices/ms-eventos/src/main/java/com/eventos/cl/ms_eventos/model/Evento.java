package com.eventos.cl.ms_eventos.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

// Mapeo JPA
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "eventos")
public class Evento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(nullable = false, length = 500)
    private String informacionGeneral;

    @Column(nullable = false)
    private Date fechaHora;

    @Column(nullable = false, length = 200)
    private String ubicacion;

    @Column(nullable = false)
    private Integer capacidadMaxima;

    @Column(nullable = false, length = 50)
    private String estado;
}
