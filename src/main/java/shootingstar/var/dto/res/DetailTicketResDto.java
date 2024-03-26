package shootingstar.var.dto.res;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
public class DetailTicketResDto {
    private LocalDateTime meetingDate;
    private String meetingLocation;
    private String organizerNickname;
    private String winnerNickname;
    private Long winningBid;
    private Double donation;
    private String meetingInfoText;
    private String meetingPromiseText;

    @Builder
    public DetailTicketResDto(LocalDateTime meetingDate, String meetingLocation, String organizerNickname,
                              String winnerNickname, Long winningBid, Double donation, String meetingInfoText,
                              String meetingPromiseText) {
        this.meetingDate = meetingDate;
        this.meetingLocation = meetingLocation;
        this.organizerNickname = organizerNickname;
        this.winnerNickname = winnerNickname;
        this.winningBid = winningBid;
        this.donation = donation;
        this.meetingInfoText = meetingInfoText;
        this.meetingPromiseText = meetingPromiseText;
    }
}