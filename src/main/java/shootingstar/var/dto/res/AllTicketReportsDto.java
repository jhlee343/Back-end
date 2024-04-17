package shootingstar.var.dto.res;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import shootingstar.var.enums.status.TicketReportStatus;

@Data
public class AllTicketReportsDto {
    private String ticketReportUUID;
    private String ticketReportNickname;
    private String ticketReportContent;
    private String ticketReportEvidenceUrl;
    private TicketReportStatus ticketReportStatus;

    @QueryProjection
    public AllTicketReportsDto(String ticketReportUUID, String ticketReportNickname, String ticketReportContent, String ticketReportEvidenceUrl, TicketReportStatus ticketReportStatus) {
        this.ticketReportUUID = ticketReportUUID;
        this.ticketReportNickname = ticketReportNickname;
        this.ticketReportContent = ticketReportContent;
        this.ticketReportEvidenceUrl = ticketReportEvidenceUrl;
        this.ticketReportStatus = ticketReportStatus;
    }
}
