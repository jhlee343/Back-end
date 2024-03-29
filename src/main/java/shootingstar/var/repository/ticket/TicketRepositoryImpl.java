package shootingstar.var.repository.ticket;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.StringUtils;
import shootingstar.var.dto.res.QTicketListResDto;
import shootingstar.var.dto.res.TicketListResDto;
import shootingstar.var.enums.type.AuctionType;
import shootingstar.var.enums.type.TicketSortType;

import java.util.List;

import static shootingstar.var.entity.ticket.QTicket.ticket;

public class TicketRepositoryImpl implements TicketRepositoryCustom{
    private final JPAQueryFactory queryFactory;
    public TicketRepositoryImpl(EntityManager em){this.queryFactory = new JPAQueryFactory(em);}

    @Override
    public List<TicketListResDto> findTicketByuserUUID(String userUUID) {
      //  return null;
        return queryFactory
                .select(new QTicketListResDto(
                        ticket.organizer.nickname,
                        ticket.auction.meetingLocation,
                        ticket.auction.meetingDate,
                        ticket.organizer.rating,
                        ticket.organizer.profileImgUrl
                ))
                .from(ticket)
                .where(userIdEq(userUUID))
                .fetch();
    }

    @Override
    public Page<TicketListResDto> findAllTicketByuserUUID(String userUUID, TicketSortType ticketSortType, String search, Pageable pageable) {
        List<TicketListResDto> content = queryFactory
                .select(new QTicketListResDto(
                        ticket.organizer.nickname,
                        ticket.auction.meetingLocation,
                        ticket.auction.meetingDate,
                        ticket.organizer.rating,
                        ticket.organizer.profileImgUrl
                ))
                .from(ticket)
                .where(userIdEq(userUUID), containName(search), ticket.auction.auctionType.eq(AuctionType.SUCCESS))
                .orderBy(orderType(ticketSortType))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(ticket.count())
                .from(ticket)
                .where(userIdEq(userUUID), containName(search));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    private OrderSpecifier orderType(TicketSortType ticketSortType) {
        return switch (ticketSortType) {
            case TIME_ASC -> new OrderSpecifier<>(Order.ASC, ticket.createdTime);
            case TIME_DESC -> new OrderSpecifier<>(Order.DESC, ticket.createdTime);
        };
    }
    private BooleanExpression userIdEq(String userUUID) {
        return userUUID != null ? ticket.winner.userUUID.eq(userUUID) : null;
    }
    private BooleanExpression containName(String search) {
        return StringUtils.hasText(search) ? ticket.winner.name.contains(search) : null;
    }
}
