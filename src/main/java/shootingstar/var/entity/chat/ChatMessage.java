package shootingstar.var.entity.chat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shootingstar.var.entity.BaseTimeEntity;

@Entity
@Getter
@NoArgsConstructor
public class ChatMessage extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chatMessageId;

    private String chatMessageUUID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;

    private String senderNickname;

    @Column(columnDefinition = "LONGTEXT")
    @NotBlank
    private String chatContent;

    @Builder
    public ChatMessage(ChatRoom chatRoom, String senderNickname, String chatContent) {
        this.chatMessageUUID = UUID.randomUUID().toString();
        this.chatRoom = chatRoom;
        this.senderNickname = senderNickname;
        this.chatContent = chatContent;
    }
}
