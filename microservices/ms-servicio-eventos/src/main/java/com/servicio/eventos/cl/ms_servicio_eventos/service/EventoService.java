package com.servicio.eventos.cl.ms_servicio_eventos.service;

import com.servicio.eventos.cl.ms_servicio_eventos.model.Evento;
import com.servicio.eventos.cl.ms_servicio_eventos.repository.EventoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class EventoService {

    // Inyecta nuestra interfaz de base de datos
    @Autowired
    private EventoRepository eventoRepository;

    // Retorna todos los eventos
    public List<Evento> obtenerTodos() {
        return eventoRepository.findAll();
    }

    // Guarda un evento (sirve tambien para actualizar si existe)
    public Evento guardar(Evento evento) {
        return eventoRepository.save(evento);
    }

    // Busca un evento, y si no esta retorna nulo en vez de error
    public Evento obtenerPorId(Long id) {
        return eventoRepository.findById(id).orElse(null);
    }

    // Elimina fisicamente el evento
    public void eliminar(Long id){
        eventoRepository.deleteById(id);
    }
}
