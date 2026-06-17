package cl.eventos.ms_tickets.service;

import cl.eventos.ms_tickets.model.Categoria;
import cl.eventos.ms_tickets.repository.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public List<Categoria> obtenerTodas() {
        return categoriaRepository.findAll();
    }

    public Optional<Categoria> obtenerPorId(Long id){
        return categoriaRepository.findById(id);
    }

    public Categoria guardar(Categoria categoria){
        return categoriaRepository.save(categoria);
    }

    public void eliminar(Long id){
        categoriaRepository.deleteById(id);
    }
}
