package shootingstar.var.repository.chat;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import shootingstar.var.dto.res.AllChatReportsDto;
import shootingstar.var.dto.res.QAllChatReportsDto;

import java.util.List;

import static org.springframework.util.StringUtils.hasText;
import static shootingstar.var.entity.chat.QChatReport.chatReport;

public class ChatReportRepositoryImpl implements ChatReportRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    public ChatReportRepositoryImpl(EntityManager em){
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<AllChatReportsDto> findAllChatReports(String search, Pageable pageable) {
        List<AllChatReportsDto> content = queryFactory
                .select(new QAllChatReportsDto(
                        chatReport.chatReportUUID,
                        chatReport.chatReportNickname,
                        chatReport.chatReportTargetNickname,
                        chatReport.chatReportContent,
                        chatReport.chatReportStatus
                ))
                .from(chatReport)
                .where(checkSearch(search))
                .orderBy(chatReport.chatReportId.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(chatReport.count())
                .from(chatReport)
                .where(checkSearch(search));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    private BooleanExpression checkSearch (String search) {
        if (!hasText(search)) {
            return null;
        }

        return chatReport.chatReportNickname.eq(search)
                .or(chatReport.chatReportTargetNickname.eq(search));
    }
}
