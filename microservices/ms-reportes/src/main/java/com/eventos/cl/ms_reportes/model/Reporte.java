package com.eventos.cl.ms_reportes.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "reportes")
public class Reporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Date fechaGeneracion;

    @Column(nullable = false, length = 100)
    private String periodo;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal ingresosTotales;

    @Column(nullable = false)
    private Integer totalTicketsVendidos;

    @Column(nullable = false, length = 150)
    private String ticketMasPopular;
}
