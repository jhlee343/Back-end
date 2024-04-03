package shootingstar.var.repository.chat;

import org.springframework.data.jpa.repository.JpaRepository;
import shootingstar.var.entity.chat.ChatMessage;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
}
