package shootingstar.var.dto.res;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
public class SaveChatMessageResDto {
    private String nickname;
    private String content;
    private LocalDateTime sendTime;

    @Builder
    public SaveChatMessageResDto(String nickname, String content, LocalDateTime sendTime) {
        this.nickname = nickname;
        this.content = content;
        this.sendTime = sendTime;
    }
}
