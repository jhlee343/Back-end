package shootingstar.var.dto.req;

import lombok.Data;

@Data
public class ChatMessageReqDto {
    private String accessToken;
    private String chatRoomUUID;
    private String message;
}
