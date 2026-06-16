package cl.eventos.ms_ordenes.service;

import cl.eventos.ms_ordenes.client.AuthClient;
import cl.eventos.ms_ordenes.client.PagoClient;
import cl.eventos.ms_ordenes.client.TicketClient;
import cl.eventos.ms_ordenes.client.UsuarioClient;
import cl.eventos.ms_ordenes.dto.DetalleRequestDTO;
import cl.eventos.ms_ordenes.dto.OrdenRequestDTO;
import cl.eventos.ms_ordenes.dto.TicketDTO;
import cl.eventos.ms_ordenes.dto.UsuarioDTO;
import cl.eventos.ms_ordenes.model.DetalleOrden;
import cl.eventos.ms_ordenes.model.Orden;
import cl.eventos.ms_ordenes.repository.DetalleOrdenRepository;
import cl.eventos.ms_ordenes.repository.OrdenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class OrdenService {

    private final OrdenRepository ordenRepository;
    private final TicketClient ticketClient;
    private final PagoClient pagoClient;
    private final AuthClient authClient;
    private final UsuarioClient usuarioClient;

    public Orden crearOrden(OrdenRequestDTO dto, String token) {

        // Validación de seguridad con el token, esto es lo que conecta con el Auth.
        Map<String, Object> authRespuesta = authClient.validarToken(token);
        if (authRespuesta == null || !(boolean) authRespuesta.get("valido")) {
            throw new RuntimeException("Acceso denegado: Token no válido.");
        }

        // Aqui se valida el Usuario por la ID de el, ademas de la conexion con el ms-Usuario.
        UsuarioDTO usuario = usuarioClient.obtenerUsuarioPorId(dto.getUsuarioId());
        if (usuario == null) {
            throw new RuntimeException("El usuario con ID " + dto.getUsuarioId() + " no existe.");
        }

        // La validacion de negocio: Aqui se verifica la existencia y stock de todos los tickets antes de procesar la compra.
        for (DetalleRequestDTO det : dto.getDetalles()) {
            TicketDTO info = ticketClient.obtenerTicketPorId(det.getTicketId());
            if (info == null) {
                throw new RuntimeException("El ticket ID " + det.getTicketId() + " no existe.");
            }
            if (info.getStock() < det.getCantidad()) {
                throw new RuntimeException("Stock insuficiente para el ticket: " + info.getId());
            }
        }

        // Creacion de la Orden.
        Orden orden = new Orden();
        orden.setUsuarioId(dto.getUsuarioId());
        orden.setEstado("PENDIENTE");

        List<DetalleOrden> listaDetalles = new ArrayList<>();
        BigDecimal totalGeneral = BigDecimal.ZERO;

        // Procesamiento de detalles y actualización de stock.
        for (DetalleRequestDTO det : dto.getDetalles()) {
            TicketDTO info = ticketClient.obtenerTicketPorId(det.getTicketId());

            DetalleOrden detalle = new DetalleOrden();
            detalle.setTicketId(det.getTicketId());
            detalle.setCantidad(det.getCantidad());
            detalle.setPrecioUnitario(info.getPrecio());
            detalle.setSubtotal(info.getPrecio().multiply(new BigDecimal(det.getCantidad())));
            detalle.setOrden(orden);

            listaDetalles.add(detalle);
            totalGeneral = totalGeneral.add(detalle.getSubtotal());
            
            ticketClient.actualizarStock(info.getId(), info.getStock() - det.getCantidad());
        }

        orden.setDetalles(listaDetalles);
        orden.setGranTotal(totalGeneral);

        return ordenRepository.save(orden);
    }

    public List<Orden> listarTodas() {
        return ordenRepository.findAll();
    }

    public Optional<Orden> obtenerPorId(Long id) {
        return ordenRepository.findById(id);
    }

    public List<Orden> obtenerPorUsuario(Long usuarioId) {
        return ordenRepository.findByUsuarioId(usuarioId);
    }

    public Optional<Orden> actualizarEstado(Long id, String nuevoEstado) {
        Optional<Orden> resultado = ordenRepository.findById(id);

        if (!resultado.isPresent()) {
            return Optional.empty();
        }

        Orden orden = resultado.get();
        orden.setEstado(nuevoEstado);

        return Optional.of(ordenRepository.save(orden));
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
