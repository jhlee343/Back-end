package shootingstar.var.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shootingstar.var.entity.Ticket;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
}
