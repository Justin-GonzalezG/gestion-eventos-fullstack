package cl.eventos.ms_pagos.service;

import cl.eventos.ms_pagos.client.OrdenClient;
import cl.eventos.ms_pagos.dto.PagoRequestDTO;
import cl.eventos.ms_pagos.dto.PagoResponseDTO;
import cl.eventos.ms_pagos.model.Pago;
import cl.eventos.ms_pagos.repository.PagoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor

public class PagoService {

    private final PagoRepository pagoRepository;
    private final OrdenClient ordenClient;

    private PagoResponseDTO mapearAResponse(Pago pago) {
        PagoResponseDTO dto = new PagoResponseDTO();

        dto.setId(pago.getId());
        dto.setOrdenId(pago.getOrdenId());
        dto.setMonto(pago.getMonto());
        dto.setMetodoPago(pago.getMetodoPago());
        dto.setEstadoPago(pago.getEstadoPago());
        dto.setFechaPago(pago.getFechaPago());

        return dto;
    }

    public List<PagoResponseDTO> findAll() {
        return pagoRepository.findAll()
                .stream()
                .map(this::mapearAResponse)
                .collect(Collectors.toList());
    }

    public PagoResponseDTO findById(Long id) {
        Pago pago = pagoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pago no encontrado con ID: " + id));

        return mapearAResponse(pago);
    }

    @Transactional
    public PagoResponseDTO save(PagoRequestDTO dto) {
        Pago pago = new Pago();

        pago.setOrdenId(dto.getOrdenId());
        pago.setMonto(dto.getMonto());
        pago.setMetodoPago(dto.getMetodoPago());

        if (dto.getMonto() != null && dto.getMonto().compareTo(BigDecimal.ZERO) > 0) {

            pago.setEstadoPago("APROBADO");

        } else {

            pago.setEstadoPago("RECHAZADO");
        }

        Pago nuevoPago = pagoRepository.save(pago);

        if ("APROBADO".equals(nuevoPago.getEstadoPago())) {
            try {

                System.out.println("Conectando con ms-ordenes para actualizar Orden ID: " + nuevoPago.getOrdenId());
                ordenClient.actualizar(nuevoPago.getOrdenId(), "PAGADA");
                System.out.println("Exito: Estado de la orden actualizado a PAGADA.");

            } catch (Exception e) {
                
                System.err.println("Error al notificar a ms-ordenes: " + e.getMessage());
            }
        }

        return mapearAResponse(nuevoPago);
    }

    public PagoResponseDTO update(Long id, PagoRequestDTO dto) {
        Pago pago = pagoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pago no encontrado con ID: " + id));

        pago.setOrdenId(dto.getOrdenId());
        pago.setMonto(dto.getMonto());
        pago.setMetodoPago(dto.getMetodoPago());

        Pago pagoActualizado = pagoRepository.save(pago);
        return mapearAResponse(pagoActualizado);
    }

    @Transactional
    public PagoResponseDTO actualizarEstado(Long id, String nuevoEstado) {
        Pago pago = pagoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se encontro el pago con ID: " + id));

        pago.setEstadoPago(nuevoEstado.toUpperCase());
        Pago actualizado = pagoRepository.save(pago);

        return mapearAResponse(actualizado);
    }

    public void delete(Long id) {

        if (!pagoRepository.existsById(id)) {

            throw new RuntimeException("No se puede eliminar. Pago no encontrado con ID: " + id);
        }

        pagoRepository.deleteById(id);
    }
}
