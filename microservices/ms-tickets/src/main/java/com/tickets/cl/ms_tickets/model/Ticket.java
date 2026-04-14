package com.tickets.cl.ms_tickets.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "ticket") // <-- Dejo esto comentado compañero, ya que con
                        // esto le ponemos el nombre de la tabla en XAMPP
@Data

public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String tipo;

    @Column(nullable = false)
    private Integer precio;

    @Column(nullable = false)
    private Integer stock;
}
