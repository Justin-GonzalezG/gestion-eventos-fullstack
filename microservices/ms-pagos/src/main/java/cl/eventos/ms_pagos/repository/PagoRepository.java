package cl.eventos.ms_pagos.repository;

import cl.eventos.ms_pagos.model.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public interface PagoRepository extends JpaRepository<Pago, Long> {

    List<Pago> findByOrdenId(Long ordenId);
}