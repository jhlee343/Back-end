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
import shootingstar.var.dto.res.AllTicketsDto;
import shootingstar.var.dto.res.QAllTicketsDto;
import shootingstar.var.dto.res.QTicketListResDto;
import shootingstar.var.dto.res.TicketListResDto;
import shootingstar.var.enums.type.AuctionType;
import shootingstar.var.enums.type.TicketSortType;

import java.util.List;

import static org.springframework.util.StringUtils.hasText;
import static shootingstar.var.entity.ticket.QTicket.ticket;

public class TicketRepositoryImpl implements TicketRepositoryCustom{
    private final JPAQueryFactory queryFactory;
    public TicketRepositoryImpl(EntityManager em){this.queryFactory = new JPAQueryFactory(em);}

    @Override
    public List<TicketListResDto> findTicketByuserUUID(String userUUID) {
//        return null;
        return queryFactory
                .select(new QTicketListResDto(
                        ticket.organizer.nickname,
                        ticket.auction.meetingLocation,
                        ticket.auction.meetingDate,
                        ticket.organizer.rating,
                        ticket.organizer.profileImgUrl,
                        ticket.ticketUUID
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
                        ticket.organizer.profileImgUrl,
                        ticket.ticketUUID
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

    @Override
    public Page<AllTicketsDto> findAllTickets(String search, Pageable pageable) {
        List<AllTicketsDto> content = queryFactory
                .select(new QAllTicketsDto(
                        ticket.ticketUUID,
                        ticket.organizer.name,
                        ticket.organizer.nickname,
                        ticket.winner.name,
                        ticket.winner.nickname,
                        ticket.auction.meetingDate,
                        ticket.ticketIsOpened
                ))
                .from(ticket)
                .where(checkSearch(search))
                .orderBy(ticket.ticketId.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(ticket.count())
                .from(ticket)
                .where(checkSearch(search));

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
        return hasText(search) ? ticket.winner.name.contains(search) : null;
    }

    private BooleanExpression checkSearch (String search) {
        if (!hasText(search)) {
            return null;
        }

        return ticket.organizer.name.eq(search)
                .or(ticket.organizer.nickname.eq(search))
                .or(ticket.winner.name.eq(search))
                .or(ticket.winner.nickname.eq(search));
    }
}
