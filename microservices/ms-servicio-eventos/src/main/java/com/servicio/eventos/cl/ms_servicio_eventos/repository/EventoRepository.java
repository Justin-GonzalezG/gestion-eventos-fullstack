package com.servicio.eventos.cl.ms_servicio_eventos.repository;

import com.servicio.eventos.cl.ms_servicio_eventos.model.Evento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventoRepository extends JpaRepository<Evento, Long> {
}
