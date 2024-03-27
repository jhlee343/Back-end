package shootingstar.var.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class TicketReport extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ticketReportId;

    private String ticketReportUUID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id")
    private Ticket ticket;

    @NotBlank
    private String ticketReportNickname;

    @Lob
    @NotBlank
    private String ticketReportContent;

    @NotBlank
    private String ticketReportEvidenceUrl;

    @Enumerated(value = EnumType.STRING)
    private TicketReportStatus ticketReportStatus;

    @Builder
    public TicketReport(Ticket ticket, String ticketReportNickname, String ticketReportContent,
                        String ticketReportEvidenceUrl) {
        this.ticketReportUUID = UUID.randomUUID().toString();
        this.ticket = ticket;
        this.ticketReportNickname = ticketReportNickname;
        this.ticketReportContent = ticketReportContent;
        this.ticketReportEvidenceUrl = ticketReportEvidenceUrl;
        this.ticketReportStatus = TicketReportStatus.STANDBY;
    }
}
