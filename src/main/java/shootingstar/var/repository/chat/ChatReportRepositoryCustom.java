package shootingstar.var.repository.chat;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import shootingstar.var.dto.res.AllChatReportsDto;

public interface ChatReportRepositoryCustom {
    Page<AllChatReportsDto> findAllChatReports(String search, Pageable pageable);
}
