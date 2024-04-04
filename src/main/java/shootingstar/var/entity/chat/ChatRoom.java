package shootingstar.var.entity.chat;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shootingstar.var.entity.BaseTimeEntity;
import shootingstar.var.entity.ticket.Ticket;

@Entity
@Getter
@NoArgsConstructor
public class ChatRoom extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chatRoomId;

    private String chatRoomUUID;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id")
    private Ticket ticket;

    private boolean chatRoomIsOpened;

    @Builder
    public ChatRoom(Ticket ticket) {
        this.chatRoomUUID = UUID.randomUUID().toString();
        this.ticket = ticket;
        this.chatRoomIsOpened = true;
    }

    public void changeChatRoomIsOpened(boolean chatRoomIsOpened) {
        this.chatRoomIsOpened = chatRoomIsOpened;
    }
}
