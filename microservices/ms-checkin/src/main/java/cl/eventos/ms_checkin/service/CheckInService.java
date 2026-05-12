package cl.eventos.ms_checkin.service;

import cl.eventos.ms_checkin.client.OrdenClient;
import cl.eventos.ms_checkin.client.TicketClient;
import cl.eventos.ms_checkin.dto.CheckInRequestDTO;
import cl.eventos.ms_checkin.dto.CheckInResponseDTO;
import cl.eventos.ms_checkin.model.CheckIn;
import cl.eventos.ms_checkin.repository.CheckInRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
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
        return mapToDTO(checkInRepository.save(checkIn));
    }

    public Optional<CheckInResponseDTO> actualizar(Long id, CheckInRequestDTO dto) {
        return checkInRepository.findById(id).map(existente -> {
            existente.setTicketId(dto.getTicketId());
            return mapToDTO(checkInRepository.save(existente));
        });
    }

    public void eliminar(Long id) {
        checkInRepository.deleteById(id);
    }

    public Optional<CheckInResponseDTO> buscarPorTicket(Long ticketId) {
        return checkInRepository.buscarPorTicketId(ticketId)
                .map(this::mapToDTO);
    }

    public Long obtenerTotalAsistentes() {
        return checkInRepository.contarTotalIngresos();
    }
}
