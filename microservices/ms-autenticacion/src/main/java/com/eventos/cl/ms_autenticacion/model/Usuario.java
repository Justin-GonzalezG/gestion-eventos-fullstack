package com.eventos.cl.ms_autenticacion.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Table(name = "usuarios") // Recuerda que con esto definimos el nombre de la Tabla en MySQL.
@Entity

public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    // Se debe agregar en el postman para agregar a ala persona.
    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    private String nombre;

    private String apellido;

    private String rol;
}
