package shootingstar.var.dto.res;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import shootingstar.var.enums.status.ChatReportStatus;

@Data
public class AllChatReportsDto {
    private String chatReportUUID;
    private String chatReportNickname;
    private String chatReportTargetNickname;
    private String chatReportContent;
    private ChatReportStatus chatReportStatus;

    @QueryProjection
    public AllChatReportsDto(String chatReportUUID, String chatReportNickname, String chatReportTargetNickname, String chatReportContent, ChatReportStatus chatReportStatus) {
        this.chatReportUUID = chatReportUUID;
        this.chatReportNickname = chatReportNickname;
        this.chatReportTargetNickname = chatReportTargetNickname;
        this.chatReportContent = chatReportContent;
        this.chatReportStatus = chatReportStatus;
    }
}
