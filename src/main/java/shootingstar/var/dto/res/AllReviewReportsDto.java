package shootingstar.var.dto.res;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import shootingstar.var.entity.ReviewReportStatus;

@Data
public class AllReviewReportsDto {
    private String reviewReportUUID;
    private String writerNickname;
    private String receiverNickname;
    private String reviewReportContent;
    private ReviewReportStatus reviewReportStatus;

    @QueryProjection
    public AllReviewReportsDto(String reviewReportUUID, String writerNickname, String receiverNickname, String reviewReportContent, ReviewReportStatus reviewReportStatus) {
        this.reviewReportUUID = reviewReportUUID;
        this.writerNickname = writerNickname;
        this.receiverNickname = receiverNickname;
        this.reviewReportContent = reviewReportContent;
        this.reviewReportStatus = reviewReportStatus;
    }
}
