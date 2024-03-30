package shootingstar.var.repository.ticket;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import shootingstar.var.entity.ticket.Ticket;

public interface TicketRepository extends JpaRepository<Ticket, Long>, TicketRepositoryCustom {
    Optional<Ticket> findByTicketUUID(String ticketUUID);
}
