package shootingstar.var.dto.res;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AllTicketsDto {
    private String ticketUUID;
    private String organizerName;
    private String organizerNickname;
    private String winnerName;
    private String winnerNickname;
    private LocalDateTime meetingDate;
    private boolean ticketIsOpened;

    @QueryProjection
    public AllTicketsDto(String ticketUUID, String organizerName, String organizerNickname, String winnerName, String winnerNickname, LocalDateTime meetingDate, boolean ticketIsOpened) {
        this.ticketUUID = ticketUUID;
        this.organizerName = organizerName;
        this.organizerNickname = organizerNickname;
        this.winnerName = winnerName;
        this.winnerNickname = winnerNickname;
        this.meetingDate = meetingDate;
        this.ticketIsOpened = ticketIsOpened;
    }
}