package shootingstar.var.repository.reviewReport;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import shootingstar.var.dto.res.AllReviewReportsDto;
import shootingstar.var.dto.res.QAllReviewReportsDto;

import java.util.List;

import static org.springframework.util.StringUtils.hasText;
import static shootingstar.var.entity.QReviewReport.reviewReport;

public class ReviewReportRepositoryImpl implements ReviewReportRepositoryCustom{
    private final JPAQueryFactory queryFactory;
    public ReviewReportRepositoryImpl(EntityManager em){
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<AllReviewReportsDto> findAllReviewReports(String search, Pageable pageable) {
        List<AllReviewReportsDto> content = queryFactory
                .select(new QAllReviewReportsDto(
                        reviewReport.reviewReportUUID,
                        reviewReport.review.writer.nickname,
                        reviewReport.review.receiver.nickname,
                        reviewReport.reviewReportContent,
                        reviewReport.reviewReportStatus
                ))
                .from(reviewReport)
                .where(checkSearch(search))
                .orderBy(reviewReport.reviewReportId.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(reviewReport.count())
                .from(reviewReport)
                .where(checkSearch(search));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    private BooleanExpression checkSearch (String search) {
        if (!hasText(search)) {
            return null;
        }

        return reviewReport.review.writer.nickname.eq(search)
                .or(reviewReport.review.receiver.nickname.eq(search));
    }
}
