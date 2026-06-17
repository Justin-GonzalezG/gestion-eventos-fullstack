package cl.eventos.ms_tickets.config;

import cl.eventos.ms_tickets.model.Categoria;
import cl.eventos.ms_tickets.model.Ticket;
import cl.eventos.ms_tickets.repository.CategoriaRepository;
import cl.eventos.ms_tickets.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

import org.springframework.context.annotation.Profile;

@Component
@RequiredArgsConstructor
@Slf4j
@Profile("!test")
public class DataInitializer implements CommandLineRunner {

    private final CategoriaRepository categoriaRepository;
    private final TicketRepository ticketRepository;

    @Override
    public void run(String... args) {

        if (categoriaRepository.count() > 0) {
            log.info("La Base de Datos ya tiene categorías, se omite la carga inicial.");
            return;
        }

        log.info("Base de Datos vacía detectada, insertando datos de prueba...");

        Categoria conciertos = categoriaRepository.save(new Categoria(null, "Conciertos", "Eventos musicales en vivo"));
        Categoria deportes = categoriaRepository.save(new Categoria(null, "Deportes", "Partidos y competencias deportivas"));
        Categoria teatro = categoriaRepository.save(new Categoria(null, "Teatro", "Obras dramáticas y musicales"));

        ticketRepository.save(new Ticket(null, "VIP Lollapalooza", new BigDecimal("150000.00"), 50, conciertos));
        ticketRepository.save(new Ticket(null, "General Rock en Conce", new BigDecimal("35000.00"), 500, conciertos));
        ticketRepository.save(new Ticket(null, "Tribuna Superclásico", new BigDecimal("25000.00"), 100, deportes));
        ticketRepository.save(new Ticket(null, "Galería Maratón", new BigDecimal("12000.00"), 200, deportes));
        ticketRepository.save(new Ticket(null, "Primera Fila: Hamlet", new BigDecimal("45000.00"), 30, teatro));

        log.info("Carga inicial completada con éxito.");
    }
}
