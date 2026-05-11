package cl.eventos.ms_ordenes.repository;

import cl.eventos.ms_ordenes.model.DetalleOrden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface DetalleOrdenRepository extends JpaRepository<DetalleOrden, Long> {
}
