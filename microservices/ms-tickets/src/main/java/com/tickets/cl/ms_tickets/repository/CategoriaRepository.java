package com.tickets.cl.ms_tickets.repository;

import com.tickets.cl.ms_tickets.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
}
