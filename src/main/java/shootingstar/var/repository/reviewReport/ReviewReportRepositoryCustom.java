package shootingstar.var.repository.reviewReport;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import shootingstar.var.dto.res.AllReviewReportsDto;

public interface ReviewReportRepositoryCustom {
    Page<AllReviewReportsDto> findAllReviewReports(String search, Pageable pageable);
}
