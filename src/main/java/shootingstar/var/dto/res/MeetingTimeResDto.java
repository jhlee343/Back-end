package shootingstar.var.dto.res;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class MeetingTimeResDto {
    private LocalDateTime startMeetingTime;

    public MeetingTimeResDto(LocalDateTime startMeetingTime) {
        this.startMeetingTime = startMeetingTime;
    }
}
