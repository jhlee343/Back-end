package shootingstar.var.entity.ticket;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shootingstar.var.entity.BaseTimeEntity;
import shootingstar.var.entity.ticket.Ticket;

@Entity
@Getter
@NoArgsConstructor
public class TicketMeetingTime extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ticketMeetingTimeId;

    private String ticketMeetingTimeUUID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id")
    private Ticket ticket;

    @NotBlank
    private String userNickname;

    @NotNull
    private LocalDateTime startMeetingTime;

    @Builder
    public TicketMeetingTime(Ticket ticket, String userNickname,
                             LocalDateTime startMeetingTime) {
        this.ticketMeetingTimeUUID = UUID.randomUUID().toString();
        this.ticket = ticket;
        this.userNickname = userNickname;
        this.startMeetingTime = startMeetingTime;
    }
}
