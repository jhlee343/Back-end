package shootingstar.var.repository.chat;

import org.springframework.data.jpa.repository.JpaRepository;
import shootingstar.var.entity.chat.ChatReport;

import java.util.Optional;

public interface ChatReportRepository extends JpaRepository<ChatReport, Long>, ChatReportRepositoryCustom {
    Optional<ChatReport> findByChatReportUUID(String chatReportUUID);
}
