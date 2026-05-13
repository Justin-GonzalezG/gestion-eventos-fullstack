package cl.eventos.ms_ordenes.service;

import cl.eventos.ms_ordenes.client.TicketClient;
import cl.eventos.ms_ordenes.dto.DetalleRequestDTO;
import cl.eventos.ms_ordenes.dto.OrdenRequestDTO;
import cl.eventos.ms_ordenes.dto.TicketDTO;
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
    private final TicketClient ticketClient;

    @Transactional
    public Orden crearOrden(OrdenRequestDTO dto) {

        Orden nuevaOrden = new Orden();
        nuevaOrden.setUsuarioId(dto.getUsuarioId());
        nuevaOrden.setEstado("PENDIENTE");

        List<DetalleOrden> listaDetalles = new ArrayList<>();
        BigDecimal totalGeneral = BigDecimal.ZERO;

        for (DetalleRequestDTO detDto : dto.getDetalles()) {

            TicketDTO ticketInfo = ticketClient.obtenerTicketPorId(detDto.getTicketId());

            if (ticketInfo.getStock() < detDto.getCantidad()) {

                throw new RuntimeException("Stock insuficiente para: " + ticketInfo.getTipo());
            }

            DetalleOrden detalle = new DetalleOrden();
            detalle.setTicketId(detDto.getTicketId());
            detalle.setCantidad(detDto.getCantidad());
            detalle.setPrecioUnitario(ticketInfo.getPrecio());

            BigDecimal subtotal = ticketInfo.getPrecio().multiply(new BigDecimal(detDto.getCantidad()));
            detalle.setSubtotal(subtotal);
            detalle.setOrden(nuevaOrden);

            listaDetalles.add(detalle);
            totalGeneral = totalGeneral.add(subtotal);

            int nuevoStock = ticketInfo.getStock() - detDto.getCantidad();
            ticketInfo.setStock(nuevoStock);

            ticketClient.actualizarStock(ticketInfo.getId(), nuevoStock);
        }

        nuevaOrden.setDetalles(listaDetalles);
        nuevaOrden.setGranTotal(totalGeneral);

        return ordenRepository.save(nuevaOrden);
    }

    public List<Orden> listarTodas() {
        return ordenRepository.findAll();
    }

    @Transactional
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

    public boolean estaPagado(Long ticketId) {
        return ordenRepository.existsByEstadoAndDetalles_TicketId("PAGADA", ticketId);
    }
}
