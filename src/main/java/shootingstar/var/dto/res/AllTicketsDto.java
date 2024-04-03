package shootingstar.var.dto.res;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AllTicketsDto {
    private String ticketUUID;
    private String organizer;
    private String winner;
    private boolean ticketIsOpened;
    private LocalDateTime meetingDate;

    @QueryProjection
    public AllTicketsDto(String ticketUUID, String organizer, String winner, boolean ticketIsOpened, LocalDateTime meetingDate) {
        this.ticketUUID = ticketUUID;
        this.organizer = organizer;
        this.winner = winner;
        this.ticketIsOpened = ticketIsOpened;
        this.meetingDate = meetingDate;
    }
}