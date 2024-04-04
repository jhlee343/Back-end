package shootingstar.var.dto.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
public class ChatReportReqDto {
    @NotBlank
    private String chatRoomUUID;

    @NotBlank
    private String chatReportContent;
}
