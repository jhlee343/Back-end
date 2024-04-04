package shootingstar.var.repository.chat;

import org.springframework.data.jpa.repository.JpaRepository;
import shootingstar.var.entity.chat.ChatReport;

public interface ChatReportRepository extends JpaRepository<ChatReport, Long> {
}
