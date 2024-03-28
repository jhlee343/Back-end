package shootingstar.var.repository.ticket;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import shootingstar.var.entity.ticket.TicketMeetingTime;

public interface TicketMeetingTimeRepository extends JpaRepository<TicketMeetingTime, Long> {
    @Query("select tmt from TicketMeetingTime tmt where tmt.ticket.ticketId = :ticketId and tmt.userNickname = :userNickname")
    Optional<TicketMeetingTime> findByTicketIdAndUserNickname(Long ticketId, String userNickname);
}
