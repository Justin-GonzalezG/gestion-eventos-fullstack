package cl.eventos.ms_recomendacion.repository;

import cl.eventos.ms_recomendacion.model.Recomendacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecomendacionRepository extends JpaRepository<Recomendacion, Long> {
}
