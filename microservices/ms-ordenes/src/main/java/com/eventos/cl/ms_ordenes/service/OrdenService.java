package com.eventos.cl.ms_ordenes.service;

import com.eventos.cl.ms_ordenes.model.Orden;
import com.eventos.cl.ms_ordenes.repository.OrdenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor

public class OrdenService {

    private final OrdenRepository ordenRepository;

    @Transactional
    public Orden crearOrden(Orden orden) {
        if (orden.getDetalles() != null) {
            orden.getDetalles().forEach(detalle -> detalle.setOrden(orden));
        }

        return ordenRepository.save(orden);
    }
    public List<Orden> listarTodas() {
        return ordenRepository.findAll();
    }

    public List<Orden> obtenerPorUsuario(Long usuarioId) {
        return ordenRepository.findByUsuarioId(usuarioId);
    }
}
