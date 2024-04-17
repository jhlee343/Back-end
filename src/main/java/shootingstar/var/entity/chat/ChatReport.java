package shootingstar.var.entity.chat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shootingstar.var.entity.BaseTimeEntity;
import shootingstar.var.enums.status.ChatReportStatus;

@Entity
@Getter
@NoArgsConstructor
public class ChatReport extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chatReportId;

    private String chatReportUUID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;

    private String chatReportNickname;

    private String chatReportTargetNickname;

    @Column(columnDefinition = "LONGTEXT")
    private String chatReportContent;

    @Enumerated(value = EnumType.STRING)
    private ChatReportStatus chatReportStatus;

    @Builder
    public ChatReport(ChatRoom chatRoom, String chatReportNickname,
                      String chatReportTargetNickname,
                      String chatReportContent) {
        this.chatReportUUID = UUID.randomUUID().toString();
        this.chatRoom = chatRoom;
        this.chatReportNickname = chatReportNickname;
        this.chatReportTargetNickname = chatReportTargetNickname;
        this.chatReportContent = chatReportContent;
        this.chatReportStatus = ChatReportStatus.STANDBY;
    }

    public void changeChatReportStatus(ChatReportStatus chatReportStatus) {
        this.chatReportStatus = chatReportStatus;
    }
}
