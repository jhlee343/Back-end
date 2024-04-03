package shootingstar.var.dto.res;

import lombok.Builder;
import lombok.Data;

@Data
public class SaveChatMessageResDto {
    private String nickname;
    private String content;

    @Builder
    public SaveChatMessageResDto(String nickname, String content) {
        this.nickname = nickname;
        this.content = content;
    }
}
