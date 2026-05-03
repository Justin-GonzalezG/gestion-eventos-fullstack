package com.eventos.cl.ms_reportes.service;

import com.eventos.cl.ms_reportes.dto.ReporteRequestDTO;
import com.eventos.cl.ms_reportes.dto.ReporteResponseDTO;
import com.eventos.cl.ms_reportes.model.Reporte;
import com.eventos.cl.ms_reportes.repository.ReporteRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ReporteService {

    private final ReporteRepository reporteRepository;

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
        // Asignamos la fecha exacta en la que se crea el reporte en el sistema
        reporte.setFechaGeneracion(new Date()); 
        reporte.setPeriodo(dto.getPeriodo());
        reporte.setIngresosTotales(dto.getIngresosTotales());
        reporte.setTotalTicketsVendidos(dto.getTotalTicketsVendidos());
        reporte.setTicketMasPopular(dto.getTicketMasPopular());

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
