package cl.eventos.ms_ordenes.service;

import cl.eventos.ms_ordenes.dto.DetalleRequestDTO;
import cl.eventos.ms_ordenes.dto.OrdenRequestDTO;
import cl.eventos.ms_ordenes.model.DetalleOrden;
import cl.eventos.ms_ordenes.model.Orden;
import cl.eventos.ms_ordenes.repository.OrdenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor

public class OrdenService {

    private final OrdenRepository ordenRepository;

    @Transactional
    public Orden crearOrden(OrdenRequestDTO dto) {

        Orden nuevaOrden = new Orden();
        nuevaOrden.setUsuarioId(dto.getUsuarioId());
        nuevaOrden.setEstado("PENDIENTE");

        List<DetalleOrden> listaDetalles = new ArrayList<>();
        BigDecimal totalGeneral = BigDecimal.ZERO;

        for (DetalleRequestDTO detDto : dto.getDetalles()) {

            DetalleOrden detalle = new DetalleOrden();
            detalle.setTicketId(detDto.getTicketId());
            detalle.setCantidad(detDto.getCantidad());
            detalle.setPrecioUnitario(detDto.getPrecioUnitario());

            BigDecimal subtotal = detDto.getPrecioUnitario().multiply(new BigDecimal(detDto.getCantidad()));
            detalle.setSubtotal(subtotal);

            detalle.setOrden(nuevaOrden);

            listaDetalles.add(detalle);
            totalGeneral = totalGeneral.add(subtotal);
        }

        nuevaOrden.setDetalles(listaDetalles);
        nuevaOrden.setGranTotal(totalGeneral);

        return ordenRepository.save(nuevaOrden);
    }

    public List<Orden> listarTodas() {
        return ordenRepository.findAll();
    }

    public Orden actualizarEstado(Long id, String nuevoEstado) {
        Orden orden = ordenRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada"));
        orden.setEstado(nuevoEstado);
        return ordenRepository.save(orden);
    }

    public List<Orden> obtenerPorUsuario(Long usuarioId) {
        return ordenRepository.findByUsuarioId(usuarioId);
    }

    public void eliminarOrden(Long id) {
        if (!ordenRepository.existsById(id)) {
            throw new RuntimeException("La orden con ID " + id + " no existe.");
        }
        ordenRepository.deleteById(id);
    }
}