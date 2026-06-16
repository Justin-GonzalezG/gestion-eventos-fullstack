package cl.eventos.ms_checkin.service;

import cl.eventos.ms_checkin.client.OrdenClient;
import cl.eventos.ms_checkin.client.TicketClient;
import cl.eventos.ms_checkin.dto.CheckInRequestDTO;
import cl.eventos.ms_checkin.dto.CheckInResponseDTO;
import cl.eventos.ms_checkin.model.CheckIn;
import cl.eventos.ms_checkin.repository.CheckInRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor

public class CheckInService {

    private final CheckInRepository checkInRepository;
    private final TicketClient ticketClient;
    private final OrdenClient ordenClient;

    private CheckInResponseDTO mapToDTO(CheckIn checkIn) {
        return new CheckInResponseDTO(
                checkIn.getId(),
                checkIn.getTicketId(),
                checkIn.getFechaIngreso()
        );
    }

    public List<CheckInResponseDTO> obtenerTodos() {
        return checkInRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public CheckInResponseDTO registrarIngreso(CheckInRequestDTO dto) {
        ticketClient.validarTicket(dto.getTicketId());

        if (checkInRepository.existsByTicketId(dto.getTicketId())) {
            throw new RuntimeException("ACCESO DENEGADO: El ticket ya fue utilizado.");
        }

        if (!ordenClient.verificarPagoTicket(dto.getTicketId())) {
            throw new RuntimeException("ACCESO DENEGADO: El ticket no figura como PAGADO.");
        }

        CheckIn checkIn = new CheckIn();
        checkIn.setTicketId(dto.getTicketId());

        CheckIn guardado = checkInRepository.save(checkIn);
        return mapToDTO(guardado);
    }

    public Optional<CheckInResponseDTO> obtenerPorId(Long id) {
        Optional<CheckIn> resultado = checkInRepository.findById(id);

        if (!resultado.isPresent()) {
            throw new RuntimeException("El registro de check-in con la ID " + id + " no existe");
        }

        return Optional.of(mapToDTO(resultado.get()));
    }

    public Optional<CheckInResponseDTO> actualizar(Long id, CheckInRequestDTO dto) {
        Optional<CheckIn> resultado = checkInRepository.findById(id);

        if (!resultado.isPresent()) {
            return Optional.empty();
        }

        CheckIn existente = resultado.get();
        existente.setTicketId(dto.getTicketId());

        CheckIn actualizado = checkInRepository.save(existente);
        return Optional.of(mapToDTO(actualizado));
    }

    public void eliminar(Long id) {
        checkInRepository.deleteById(id);
    }

    public Optional<CheckInResponseDTO> buscarPorTicket(Long ticketId) {
        Optional<CheckIn> resultado = checkInRepository.buscarPorTicketId(ticketId);

        if (!resultado.isPresent()) {
            return Optional.empty();
        }

        return Optional.of(mapToDTO(resultado.get()));
    }

    public Long obtenerTotalAsistentes() {
        return checkInRepository.contarTotalIngresos();
    }
}
