package shootingstar.var.dto.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MeetingTimeSaveReqDto {
    @NotBlank
    private String ticketUUID;

    @NotBlank
    private String startMeetingTime;
}
