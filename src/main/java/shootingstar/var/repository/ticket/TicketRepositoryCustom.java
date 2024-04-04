package shootingstar.var.repository.ticket;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import shootingstar.var.dto.res.AllTicketsDto;
import shootingstar.var.dto.res.TicketListResDto;
import shootingstar.var.enums.type.TicketSortType;

import java.util.List;

public interface TicketRepositoryCustom {
    List<TicketListResDto> findTicketByuserUUID(String userUUID);
    Page<TicketListResDto> findAllTicketByuserUUID(String userUUID, TicketSortType ticketSortType, String search, Pageable pageable);
    Page<AllTicketsDto> findAllTickets(String search, Pageable pageable);
}
