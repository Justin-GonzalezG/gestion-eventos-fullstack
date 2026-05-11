package cl.eventos.ms_tickets.service;

import cl.eventos.ms_tickets.dto.TicketRequestDTO;
import cl.eventos.ms_tickets.dto.TicketResponseDTO;
import cl.eventos.ms_tickets.model.Categoria;
import cl.eventos.ms_tickets.model.Ticket;
import cl.eventos.ms_tickets.repository.CategoriaRepository;
import cl.eventos.ms_tickets.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final CategoriaRepository categoriaRepository;

    private TicketResponseDTO mapToDTO(Ticket ticket) {
        return new TicketResponseDTO(

                ticket.getId(),
                ticket.getTipo(),
                ticket.getPrecio(),
                ticket.getStock(),
                ticket.getCategoria() != null ? ticket.getCategoria().getNombre() : "Sin Categoría"

        );
    }

    public List<TicketResponseDTO> obtenerTodos(){
        return ticketRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public Optional<TicketResponseDTO> obtenerPorId(Long id){
        return ticketRepository.findById(id).map(this::mapToDTO);
    }

    public TicketResponseDTO guardar(TicketRequestDTO dto){
        Categoria categoria = categoriaRepository
                .findById(dto.getCategoriaId())
                .orElseThrow(() -> new RuntimeException(
                        "Categoría NO encontrada con id: " + dto.getCategoriaId()));

        Ticket ticket = new Ticket();
        ticket.setTipo(dto.getTipo());
        ticket.setPrecio(dto.getPrecio());
        ticket.setStock(dto.getStock());
        ticket.setCategoria(categoria);

        return mapToDTO(ticketRepository.save(ticket));
    }

    public Optional<TicketResponseDTO> actualizar(Long id, TicketRequestDTO dto){
        return ticketRepository.findById(id).map(existente -> {
            Categoria categoria = categoriaRepository
                    .findById(dto.getCategoriaId())
                    .orElseThrow(() -> new RuntimeException(
                            "Categoría NO encontrada con id: " + dto.getCategoriaId()));

            existente.setTipo(dto.getTipo());
            existente.setPrecio(dto.getPrecio());
            existente.setStock(dto.getStock());
            existente.setCategoria(categoria);

            return mapToDTO(ticketRepository.save(existente));
        });
    }

    public void eliminar(Long id){
        ticketRepository.deleteById(id);
    }

    public List<TicketResponseDTO> buscarPorTipo(String texto) {
        return ticketRepository.findByTipoContainingIgnoreCase(texto)
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public List<TicketResponseDTO> buscarPorCategoria(Long categoriaId) {
        return ticketRepository.findByCategoriaId(categoriaId)
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public List<TicketResponseDTO> buscarBajoPresupuesto(BigDecimal precioMax) {
        return ticketRepository.findTicketsBajoPresupuesto(precioMax)
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public void actualizarSoloStock(Long id, Integer nuevoStock) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket no encontrado"));
        ticket.setStock(nuevoStock); // Asegúrate de que el campo se llame 'stock' en tu modelo
        ticketRepository.save(ticket);
    }
}
