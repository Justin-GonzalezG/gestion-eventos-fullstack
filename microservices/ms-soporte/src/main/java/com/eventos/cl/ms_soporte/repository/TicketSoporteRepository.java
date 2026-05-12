package com.eventos.cl.ms_soporte.repository;

import com.eventos.cl.ms_soporte.model.TicketSoporte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketSoporteRepository extends JpaRepository<TicketSoporte, Long> {
}
