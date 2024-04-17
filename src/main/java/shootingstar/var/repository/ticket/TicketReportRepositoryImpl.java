package shootingstar.var.repository.ticket;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import shootingstar.var.dto.res.AllTicketReportsDto;
import shootingstar.var.dto.res.QAllTicketReportsDto;

import java.util.List;

import static org.springframework.util.StringUtils.hasText;
import static shootingstar.var.entity.ticket.QTicketReport.ticketReport;

public class TicketReportRepositoryImpl implements TicketReportRepositoryCustom{
    private final JPAQueryFactory queryFactory;
    public TicketReportRepositoryImpl(EntityManager em){
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<AllTicketReportsDto> findAllTicketReports(String search, Pageable pageable) {
        List<AllTicketReportsDto> content = queryFactory
                .select(new QAllTicketReportsDto(
                        ticketReport.ticketReportUUID,
                        ticketReport.ticketReportNickname,
                        ticketReport.ticketReportContent,
                        ticketReport.ticketReportEvidenceUrl,
                        ticketReport.ticketReportStatus
                ))
                .from(ticketReport)
                .where(checkSearch(search))
                .orderBy(ticketReport.ticketReportId.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(ticketReport.count())
                .from(ticketReport)
                .where(checkSearch(search));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    private BooleanExpression checkSearch (String search) {
        if (!hasText(search)) {
            return null;
        }

        return ticketReport.ticketReportNickname.eq(search);
    }
}
