package shootingstar.var.repository.chat;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import shootingstar.var.entity.chat.ChatRoom;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long>, ChatRoomRepositoryCustom {
    Optional<ChatRoom> findByChatRoomUUID(String chatRoomUUID);
}
