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

@Service
@RequiredArgsConstructor
public class OrdenService {

    private final OrdenRepository ordenRepository;
    private final TicketClient ticketClient;
    private final PagoClient pagoClient;
    private final AuthClient authClient;
    private final UsuarioClient usuarioClient;

    @Transactional
    public Orden crearOrden(OrdenRequestDTO dto, String token) {

        Map<String, Object> authRespuesta = authClient.validarToken(token);

        if (authRespuesta == null || !(boolean) authRespuesta.get("valido")) {

            throw new RuntimeException("Error: Acceso denegado. Debes estar autenticado para crear órdenes.");
        }

        Orden orden = new Orden();
        orden.setUsuarioId(dto.getUsuarioId());
        orden.setEstado("PENDIENTE");
        List<DetalleOrden> listaDetalles = new ArrayList<>();
        BigDecimal totalGeneral = BigDecimal.ZERO;

        for (DetalleRequestDTO det : dto.getDetalles()) {

            TicketDTO info = ticketClient.obtenerTicketPorId(det.getTicketId());

            DetalleOrden detalle = new DetalleOrden();
            detalle.setTicketId(det.getTicketId());
            detalle.setCantidad(det.getCantidad());
            detalle.setPrecioUnitario(info.getPrecio());

            BigDecimal subtotal = info.getPrecio().multiply(new BigDecimal(det.getCantidad()));
            detalle.setSubtotal(subtotal);
            detalle.setOrden(orden);
            listaDetalles.add(detalle);
            totalGeneral = totalGeneral.add(subtotal);

            int nuevoStock = info.getStock() - det.getCantidad();
            ticketClient.actualizarStock(info.getId(), nuevoStock);
        }

        orden.setDetalles(listaDetalles);
        orden.setGranTotal(totalGeneral);
        Orden guardada = ordenRepository.save(orden);

        try {

            System.out.println("Contactando a ms-pagos para la orden: " + guardada.getId());
            pagoClient.buscarPagoPorOrdenId(guardada.getId());
            System.out.println("Pago Aceptado");

        } catch (Exception e) {

            System.out.println("Pago RECHAZADO.");
        }

        try {

            System.out.println("Verificando usuario " + dto.getUsuarioId() + " con ms-usuarios...");
            UsuarioDTO usuario = usuarioClient.obtenerUsuarioPorId(dto.getUsuarioId());
            System.out.println("¡Usuario confirmado! Nombre: " + usuario.getNombre());

        } catch (Exception e) {

            throw new RuntimeException("No se puede crear la orden. El usuario ID " + dto.getUsuarioId() + " no existe o ms-usuarios está caído.");
        }

        return guardada;
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

    public Orden obtenerPorId(Long id) {
        return ordenRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada con ID: " + id));
    }
}
