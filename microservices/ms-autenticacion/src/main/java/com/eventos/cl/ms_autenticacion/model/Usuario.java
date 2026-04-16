// Agrego los comentarios de guia por si no se entiende que hacen alguna lineas de codigo.

package com.eventos.cl.ms_autenticacion.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Table(name = "usuarios") // Recuerda que con esto definimos el nombre de la Tabla en MySQL.
@Entity

public class Usuario {

    @Id // Con esto vamos a definir que sera la llave primaria de la Tabla.
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Con esto hacemos un ID Automatico.
    private long id; // Y este es el Identificador unico

    @Column(unique = true, nullable = false) // Con esto el nombre si o si es Unico y Obligatorio.
    private String username;

    @Column(nullable = false)
    private String password;

    private String nombre;

    private String apellido;

    private String rol; // Esto nos definira el rol de la persona.
}