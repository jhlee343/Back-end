package shootingstar.var.dto.res;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
    private BigDecimal donation;
    private String meetingInfoText;
    private String meetingPromiseText;
    private boolean winnerIsPushed;
    private boolean organizerIsPushed;
    private String chatRoomUUID;

    @Builder
    public DetailTicketResDto(LocalDateTime meetingDate, String meetingLocation, String organizerNickname,
                              String winnerNickname, Long winningBid, BigDecimal donation, String meetingInfoText,
                              String meetingPromiseText, boolean winnerIsPushed, boolean organizerIsPushed, String chatRoomUUID) {
        this.meetingDate = meetingDate;
        this.meetingLocation = meetingLocation;
        this.organizerNickname = organizerNickname;
        this.winnerNickname = winnerNickname;
        this.winningBid = winningBid;
        this.donation = donation.setScale(2, RoundingMode.HALF_UP);
        this.meetingInfoText = meetingInfoText;
        this.meetingPromiseText = meetingPromiseText;
        this.winnerIsPushed = winnerIsPushed;
        this.organizerIsPushed = organizerIsPushed;
        this.chatRoomUUID = chatRoomUUID;
    }
}