package shootingstar.var.repository.ticket;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import shootingstar.var.dto.res.AllTicketReportsDto;

public interface TicketReportRepositoryCustom {
    Page<AllTicketReportsDto> findAllTicketReports(String search, Pageable pageable);
}
