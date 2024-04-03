package shootingstar.var.repository.chat;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import shootingstar.var.entity.chat.ChatMessage;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    @Query("select cm from ChatMessage cm where cm.chatRoom.chatRoomId = :chatRoomId")
    List<ChatMessage> findByChatRoomId(Long chatRoomId);
}
