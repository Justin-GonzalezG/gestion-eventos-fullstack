package com.servicio.eventos.cl.ms_servicio_eventos.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;

@Entity
@Table(name = "eventos")
@Data
public class Evento {

    // Define la llave primaria (ID)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Columna obligatoria para el titulo del evento
    @Column(nullable = false)
    private String nombre;

    // Detalles sobre de que trata el evento
    @Column(nullable = false)
    private String descripcion;

    // Fecha y hora programada del evento
    @Column(nullable = false)
    private Date fechaHora;

    // Lugar fisico o enlace virtual
    @Column(nullable = false)
    private String ubicacion;

    // Cantidad maxima de asientos
    @Column(nullable = false)
    private Integer capacidadMaxima;

    // Activo, Cancelado, o Finalizado
    @Column(nullable = false)
    private String estado;
}
