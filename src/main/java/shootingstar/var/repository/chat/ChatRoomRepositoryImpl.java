package shootingstar.var.repository.chat;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import shootingstar.var.dto.res.AllChatRoomsDto;
import shootingstar.var.dto.res.QAllChatRoomsDto;

import java.util.List;

import static org.springframework.util.StringUtils.hasText;
import static shootingstar.var.entity.chat.QChatRoom.chatRoom;

public class ChatRoomRepositoryImpl implements ChatRoomRepositoryCustom{
    private final JPAQueryFactory queryFactory;
    public ChatRoomRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<AllChatRoomsDto> findAllChatRooms(String search, Pageable pageable) {
        List<AllChatRoomsDto> content = queryFactory
                .select(new QAllChatRoomsDto(
                        chatRoom.chatRoomUUID,
                        chatRoom.ticket.organizer.name,
                        chatRoom.ticket.organizer.nickname,
                        chatRoom.ticket.winner.name,
                        chatRoom.ticket.winner.nickname,
                        chatRoom.ticket.auction.meetingDate,
                        chatRoom.chatRoomIsOpened
                ))
                .from(chatRoom)
                .where(checkSearch(search))
                .orderBy(chatRoom.chatRoomId.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(chatRoom.count())
                .from(chatRoom)
                .where(checkSearch(search));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    private BooleanExpression checkSearch (String search) {
        if (!hasText(search)) {
            return null;
        }

        return chatRoom.ticket.organizer.name.eq(search)
                .or(chatRoom.ticket.organizer.nickname.eq(search))
                .or(chatRoom.ticket.winner.name.eq(search))
                .or(chatRoom.ticket.winner.nickname.eq(search));
    }
}
