package cl.eventos.ms_reportes.service;

import cl.eventos.ms_reportes.dto.ReporteRequestDTO;
import cl.eventos.ms_reportes.dto.ReporteResponseDTO;
import cl.eventos.ms_reportes.client.OrdenClient;
import cl.eventos.ms_reportes.client.PagoClient;
import cl.eventos.ms_reportes.client.TicketClient;
import cl.eventos.ms_reportes.dto.*;
import cl.eventos.ms_reportes.model.Reporte;
import cl.eventos.ms_reportes.repository.ReporteRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ReporteService {

    private final ReporteRepository reporteRepository;
    
    // Inyectamos las 3 antenas Feign
    private final PagoClient pagoClient;
    private final OrdenClient ordenClient;
    private final TicketClient ticketClient;

    private ReporteResponseDTO mapearAResponse(Reporte reporte) {
        return new ReporteResponseDTO(
                reporte.getId(),
                reporte.getFechaGeneracion(),
                reporte.getPeriodo(),
                reporte.getIngresosTotales(),
                reporte.getTotalTicketsVendidos(),
                reporte.getTicketMasPopular()
        );
    }

    public List<ReporteResponseDTO> obtenerTodos() {
        return reporteRepository.findAll()
                .stream()
                .map(this::mapearAResponse)
                .collect(Collectors.toList());
    }

    public Optional<ReporteResponseDTO> obtenerPorId(Long id) {
        return reporteRepository.findById(id).map(this::mapearAResponse);
    }

    public ReporteResponseDTO guardar(ReporteRequestDTO dto) {
        Reporte reporte = new Reporte();
        reporte.setFechaGeneracion(new Date()); 
        reporte.setPeriodo(dto.getPeriodo());

        // 1. Calcular Ingresos Totales Automáticamente desde ms-pagos
        BigDecimal ingresosCalculados = BigDecimal.ZERO;
        try {
            List<PagoDTO> pagos = pagoClient.listarPagos();
            for (PagoDTO pago : pagos) {
                // Solo sumar si está aprobado
                if ("APROBADO".equalsIgnoreCase(pago.getEstadoPago()) && pago.getMonto() != null) {
                    ingresosCalculados = ingresosCalculados.add(pago.getMonto());
                }
            }
        } catch (Exception e) {
            System.out.println("Error conectando con ms-pagos: " + e.getMessage());
        }
        reporte.setIngresosTotales(ingresosCalculados);

        // 2. Calcular Tickets Vendidos y Ticket Más Popular desde ms-ordenes
        int totalTicketsCalculados = 0;
        String nombreTicketMasPopular = "Desconocido";
        
        try {
            List<OrdenDTO> ordenes = ordenClient.listarOrdenes();
            Map<Long, Integer> conteoTickets = new HashMap<>();

            for (OrdenDTO orden : ordenes) {
                if (orden.getDetalles() != null) {
                    for (DetalleOrdenDTO detalle : orden.getDetalles()) {
                        if (detalle.getCantidad() != null && detalle.getTicketId() != null) {
                            totalTicketsCalculados += detalle.getCantidad();
                            conteoTickets.put(detalle.getTicketId(), 
                                conteoTickets.getOrDefault(detalle.getTicketId(), 0) + detalle.getCantidad());
                        }
                    }
                }
            }

            // Buscar el ID del ticket que más se vendió
            Long idMasVendido = null;
            int maxVentas = 0;
            for (Map.Entry<Long, Integer> entry : conteoTickets.entrySet()) {
                if (entry.getValue() > maxVentas) {
                    maxVentas = entry.getValue();
                    idMasVendido = entry.getKey();
                }
            }

            // 3. Viajar a ms-tickets para descubrir el nombre del ticket más popular
            if (idMasVendido != null) {
                try {
                    TicketDTO ticketDTO = ticketClient.obtenerTicketPorId(idMasVendido);
                    if (ticketDTO != null && ticketDTO.getTipo() != null) {
                        nombreTicketMasPopular = ticketDTO.getTipo();
                    } else {
                        nombreTicketMasPopular = "Ticket ID " + idMasVendido;
                    }
                } catch (Exception e) {
                    System.out.println("Error conectando con ms-tickets: " + e.getMessage());
                    nombreTicketMasPopular = "Ticket ID " + idMasVendido;
                }
            }

        } catch (Exception e) {
            System.out.println("Error conectando con ms-ordenes: " + e.getMessage());
        }

        reporte.setTotalTicketsVendidos(totalTicketsCalculados);
        reporte.setTicketMasPopular(nombreTicketMasPopular);

        Reporte guardado = reporteRepository.save(reporte);
        return mapearAResponse(guardado);
    }

    public Optional<ReporteResponseDTO> actualizar(Long id, ReporteRequestDTO dto) {
        return reporteRepository.findById(id).map(reporte -> {
            // No actualizamos la fecha de generacion, solo los datos
            reporte.setPeriodo(dto.getPeriodo());
            reporte.setIngresosTotales(dto.getIngresosTotales());
            reporte.setTotalTicketsVendidos(dto.getTotalTicketsVendidos());
            reporte.setTicketMasPopular(dto.getTicketMasPopular());
            
            Reporte actualizado = reporteRepository.save(reporte);
            return mapearAResponse(actualizado);
        });
    }

    public void eliminar(Long id) {
        reporteRepository.deleteById(id);
    }
}

    public void eliminar(Long id) {
        reporteRepository.deleteById(id);
    }
}
