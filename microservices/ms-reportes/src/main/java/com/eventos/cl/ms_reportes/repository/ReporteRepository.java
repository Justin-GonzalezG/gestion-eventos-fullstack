package com.eventos.cl.ms_reportes.repository;

import com.eventos.cl.ms_reportes.model.Reporte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReporteRepository extends JpaRepository<Reporte, Long> {
}
