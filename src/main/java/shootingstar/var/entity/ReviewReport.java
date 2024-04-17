package shootingstar.var.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jdk.jfr.Enabled;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
public class ReviewReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewReportId;

    @NotNull
    private String reviewReportUUID;

    @ManyToOne
    @JoinColumn(name = "review_id")
    private Review review;

    @NotNull
    private String reviewReportContent;

    @NotNull
    @Enumerated(value = EnumType.STRING)
    private ReviewReportStatus reviewReportStatus;

    @Builder
    public ReviewReport(Review review, String reviewReportContent, ReviewReportStatus reviewReportStatus){
        this.reviewReportUUID= UUID.randomUUID().toString();
        this.review = review;
        this.reviewReportContent=reviewReportContent;
        this.reviewReportStatus = reviewReportStatus;
    }

    public void changeReviewReportStatus(ReviewReportStatus reviewReportStatus) {
        this.reviewReportStatus = reviewReportStatus;
    }
}
