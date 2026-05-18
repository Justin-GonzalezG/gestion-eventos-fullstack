package cl.eventos.ms_pagos.service;

import cl.eventos.ms_pagos.client.AuthClient;
import cl.eventos.ms_pagos.client.OrdenClient;
import cl.eventos.ms_pagos.dto.OrdenDTO;
import cl.eventos.ms_pagos.dto.PagoRequestDTO;
import cl.eventos.ms_pagos.dto.PagoResponseDTO;
import cl.eventos.ms_pagos.model.Pago;
import cl.eventos.ms_pagos.repository.PagoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PagoService {

    private final PagoRepository pagoRepository;
    private final OrdenClient ordenClient;
    private final AuthClient authClient;

    @Transactional
    public PagoResponseDTO save(PagoRequestDTO dto, String token) {

        Map<String, Object> authRespuesta = authClient.validarToken(token);

        if (authRespuesta == null || !(boolean) authRespuesta.get("valido")) {
            throw new RuntimeException("Acceso denegado. Token inválido o expirado.");
        }

        OrdenDTO ordenReal = ordenClient.buscarPorId(dto.getOrdenId());

        if (dto.getMonto() == null || dto.getMonto().compareTo(ordenReal.getGranTotal()) < 0) {

            ordenClient.actualizar(dto.getOrdenId(), "RECHAZADO");
            throw new RuntimeException("Pago RECHAZADO. Motivo: Monto insuficiente. La orden requiere: $" + ordenReal.getGranTotal());
        }

        Pago pago = new Pago();
        pago.setOrdenId(dto.getOrdenId());
        pago.setMonto(dto.getMonto());
        pago.setMetodoPago(dto.getMetodoPago());
        pago.setEstadoPago("APROBADO");

        ordenClient.actualizar(dto.getOrdenId(), "PAGADA");

        Pago nuevoPago = pagoRepository.save(pago);
        return mapearAResponse(nuevoPago);
    }

    public PagoResponseDTO update(Long id, PagoRequestDTO dto) {
        Pago pago = pagoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pago no encontrado"));
        pago.setMonto(dto.getMonto());
        pago.setMetodoPago(dto.getMetodoPago());
        return mapearAResponse(pagoRepository.save(pago));
    }

    public Optional<PagoResponseDTO> updateOptional(Long id, PagoRequestDTO dto) {
        return pagoRepository.findById(id).map(pago -> {
            pago.setMonto(dto.getMonto());
            pago.setMetodoPago(dto.getMetodoPago());
            return mapearAResponse(pagoRepository.save(pago));
        });
    }

    @Transactional
    public PagoResponseDTO actualizarEstado(Long id, String nuevoEstado) {
        Pago pago = pagoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pago no encontrado"));
        pago.setEstadoPago(nuevoEstado.toUpperCase());
        return mapearAResponse(pagoRepository.save(pago));
    }

    @Transactional
    public Optional<PagoResponseDTO> actualizarEstadoOptional(Long id, String nuevoEstado) {
        return pagoRepository.findById(id).map(pago -> {
            pago.setEstadoPago(nuevoEstado.toUpperCase());
            return mapearAResponse(pagoRepository.save(pago));
        });
    }

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
        return pagoRepository.findAll().stream().map(this::mapearAResponse).collect(Collectors.toList());
    }

    public PagoResponseDTO findById(Long id) {
        return mapearAResponse(pagoRepository.findById(id).orElseThrow());
    }

    public Optional<PagoResponseDTO> findByIdOptional(Long id) {
        return pagoRepository.findById(id).map(this::mapearAResponse);
    }

    public boolean existsById(Long id) {
        return pagoRepository.existsById(id);
    }

    public void delete(Long id) {
        pagoRepository.deleteById(id);
    }
}
