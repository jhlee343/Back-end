package shootingstar.var.dto.res;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AllTicketsDto {
    private String ticketUUID;
    private String organizer;
    private String winner;
    private LocalDateTime meetingDate;
    private boolean ticketIsOpened;

    @QueryProjection
    public AllTicketsDto(String ticketUUID, String organizer, String winner, boolean ticketIsOpened, LocalDateTime meetingDate) {
        this.ticketUUID = ticketUUID;
        this.organizer = organizer;
        this.winner = winner;
        this.meetingDate = meetingDate;
        this.ticketIsOpened = ticketIsOpened;
    }
}