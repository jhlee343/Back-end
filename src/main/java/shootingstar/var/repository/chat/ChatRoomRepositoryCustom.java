package shootingstar.var.repository.chat;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import shootingstar.var.dto.res.AllChatRoomsDto;

public interface ChatRoomRepositoryCustom {
    Page<AllChatRoomsDto> findAllChatRooms(String search, Pageable pageable);
}
