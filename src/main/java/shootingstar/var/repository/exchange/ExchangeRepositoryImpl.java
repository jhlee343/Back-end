package shootingstar.var.repository.exchange;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import shootingstar.var.dto.res.AllExchangesDto;
import shootingstar.var.dto.res.QAllExchangesDto;

import java.util.List;

import static org.springframework.util.StringUtils.hasText;
import static shootingstar.var.entity.QExchange.exchange;
import static shootingstar.var.enums.status.ExchangeStatus.STANDBY;

public class ExchangeRepositoryImpl implements ExchangeRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public ExchangeRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<AllExchangesDto> findAllExchanges(String search, Pageable pageable) {
        List<AllExchangesDto> content = queryFactory
                .select(new QAllExchangesDto(
                        exchange.exchangeUUID,
                        exchange.user.name,
                        exchange.user.nickname,
                        exchange.exchangePoint,
                        exchange.exchangeBank,
                        exchange.exchangeAccount,
                        exchange.exchangeAccountHolder,
                        exchange.createdTime
                ))
                .from(exchange)
                .where(exchange.exchangeStatus.eq(STANDBY), checkSearch(search))
                .orderBy(exchange.exchangeId.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(exchange.count())
                .from(exchange)
                .where(exchange.exchangeStatus.eq(STANDBY), checkSearch(search));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    private BooleanExpression checkSearch (String search) {
        if (!hasText(search)) {
            return null;
        }

        return exchange.user.name.eq(search)
                .or(exchange.user.nickname.eq(search));
    }
}