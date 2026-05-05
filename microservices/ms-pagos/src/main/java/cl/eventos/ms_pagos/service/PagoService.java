package cl.eventos.ms_pagos.service;

import cl.eventos.ms_pagos.dto.PagoRequestDTO;
import cl.eventos.ms_pagos.dto.PagoResponseDTO;
import cl.eventos.ms_pagos.model.Pago;
import cl.eventos.ms_pagos.repository.PagoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor

public class PagoService {

    private final PagoRepository pagoRepository;

    public List<PagoResponseDTO> findAll() {
        return pagoRepository.findAll()

                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

    }

    public PagoResponseDTO findById(Long id) {
        Pago pago = pagoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pago no encontrado con ID: " + id));
        return convertToDTO(pago);
    }

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

        return convertToDTO(nuevoPago);
    }

    public PagoResponseDTO update(Long id, PagoRequestDTO dto) {
        Pago pago = pagoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se puede actualizar. Pago no encontrado con ID: " + id));

        pago.setOrdenId(dto.getOrdenId());
        pago.setMonto(dto.getMonto());
        pago.setMetodoPago(dto.getMetodoPago());

        Pago pagoActualizado = pagoRepository.save(pago);

        return convertToDTO(pagoActualizado);
    }

    private PagoResponseDTO convertToDTO(Pago pago) {
        PagoResponseDTO dto = new PagoResponseDTO();

        dto.setId(pago.getId());
        dto.setOrdenId(pago.getOrdenId());
        dto.setMonto(pago.getMonto());
        dto.setMetodoPago(pago.getMetodoPago());
        dto.setEstadoPago(pago.getEstadoPago());
        dto.setFechaPago(pago.getFechaPago());

        return dto;
    }

    public void delete(Long id) {
        Pago pago = pagoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se puede eliminar. Pago no encontrado con ID: " + id));

        pagoRepository.delete(pago);
    }
}