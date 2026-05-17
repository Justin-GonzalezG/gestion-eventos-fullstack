package com.eventos.cl.ms_recomendacion.repository;

import com.eventos.cl.ms_recomendacion.model.Recomendacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecomendacionRepository extends JpaRepository<Recomendacion, Long> {
}
