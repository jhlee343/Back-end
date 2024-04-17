package shootingstar.var.repository.reviewReport;

import org.springframework.data.jpa.repository.JpaRepository;
import shootingstar.var.entity.ReviewReport;

import java.util.Optional;

public interface ReviewReportRepository extends JpaRepository<ReviewReport, Long>, ReviewReportRepositoryCustom {
    Optional <ReviewReport> findByReviewReportUUID(String reviewReportUUID);
}
