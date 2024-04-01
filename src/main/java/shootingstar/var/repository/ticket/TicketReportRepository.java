package shootingstar.var.repository.ticket;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import shootingstar.var.entity.ticket.TicketReport;

public interface TicketReportRepository extends JpaRepository<TicketReport, Long> {
    @Query("select tr from TicketReport tr where tr.ticket.ticketId = :ticketId and tr.ticketReportNickname = :ticketReportNickname")
    Optional<TicketReport> findByTicketIdAndTicketReportNickname(Long ticketId, String ticketReportNickname);
}