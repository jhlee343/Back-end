package shootingstar.var.repository;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import shootingstar.var.dto.res.*;
import shootingstar.var.enums.type.AuctionSortType;
import shootingstar.var.enums.type.AuctionType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.querydsl.core.types.dsl.DateTimePath.*;
import static shootingstar.var.entity.QAuction.auction;
import static shootingstar.var.entity.QUser.user;

public class AuctionRepositoryImpl implements AuctionRepositoryCustom{
    private final JPAQueryFactory queryFactory;
    public AuctionRepositoryImpl(EntityManager em){
        this.queryFactory = new JPAQueryFactory(em);
    }
    @Override
    public Page<UserAuctionSuccessResDto> findAllSuccessBeforeByUserUUID(String userUUID, Pageable pageable) {
        List<UserAuctionSuccessResDto> content = queryFactory
                .select(new QUserAuctionSuccessResDto(
                        auction.user.profileImgUrl,
                        auction.user.nickname,
                        auction.meetingDate,
                        user.nickname
                ))
                .from(auction)
                .where(currentHighestBidderIdEq(userUUID), auction.auctionType.eq(AuctionType.SUCCESS))
                .orderBy(auction.meetingDate.desc())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(auction.count())
                .from(auction)
                .where(currentHighestBidderIdEq(userUUID), auction.auctionType.eq(AuctionType.SUCCESS));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    //만남 전 만남후 api 두개 만들어야함
    @Override
    public Page<UserAuctionSuccessResDto> findAllSuccessAfterByUserUUID(String userUUID, Pageable pageable) {
        List<UserAuctionSuccessResDto> content = queryFactory
                .select(new QUserAuctionSuccessResDto(
                        auction.user.profileImgUrl,
                        auction.user.nickname,
                        auction.meetingDate,
                        user.nickname
                ))
                .from(auction)
                .where(currentHighestBidderIdEq(userUUID),auction.auctionType.eq(AuctionType.SUCCESS))
                .orderBy(auction.meetingDate.asc())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(auction.count())
                .from(auction)
                .where(currentHighestBidderIdEq(userUUID),auction.auctionType.eq(AuctionType.SUCCESS));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    @Override
    public List<UserAuctionParticipateResDto> findParticipateList(String userUUID, Pageable pageable) {
        return queryFactory
                .select(new QUserAuctionParticipateResDto(
                        auction.user.profileImgUrl,
                        auction.user.nickname,
                        auction.createdTime,
                        auction.bidCount,
                        auction.currentHighestBidAmount
                ))
                .from(auction)
                .where(auction.auctionType.eq(AuctionType.PROGRESS))
                .fetch();
    }

    @Override
    public Page<UserAuctionParticipateResDto> findAllParticipateByUserUUID(String userUUID, Pageable pageable) {
        return null;
    }


    //vipUser auction
    @Override
    public Page<UserAuctionSuccessResDto> findAllVipSuccessBeforeByUserUUID(String userUUID, Pageable pageable) {
        //만남 전 - 가까운 날짜 순

        List<UserAuctionSuccessResDto> content = queryFactory
                .select(new QUserAuctionSuccessResDto(
                        auction.user.profileImgUrl,
                        auction.user.nickname,
                        auction.createdTime,
                        auction.currentHighestBidderUUID
                ))
                .from(auction)
                .where(vipUserUUIDEq(userUUID),auction.auctionType.eq(AuctionType.SUCCESS),meetingDateBefore())
                .orderBy(auction.meetingDate.asc())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(auction.count())
                .from(auction)
                .where(vipUserUUIDEq(userUUID),auction.auctionType.eq(AuctionType.SUCCESS),meetingDateBefore());

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    @Override
    public Page<UserAuctionSuccessResDto> findAllVipSuccessAfterByUserUUID(String userUUID, Pageable pageable) {
        //만남 후 - 최근 날짜 순
        List<UserAuctionSuccessResDto> content = queryFactory
                .select(new QUserAuctionSuccessResDto(
                        auction.user.profileImgUrl,
                        auction.user.nickname,
                        auction.createdTime,
                        auction.currentHighestBidderUUID
                ))
                .from(auction)
                .where(vipUserUUIDEq(userUUID),auction.auctionType.eq(AuctionType.SUCCESS),meetingDateAfter())
                .orderBy(auction.meetingDate.desc())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(auction.count())
                .from(auction)
                .where(vipUserUUIDEq(userUUID),auction.auctionType.eq(AuctionType.SUCCESS),meetingDateAfter());

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    @Override
    public Page<UserAuctionParticipateResDto> findAllVipProgressByUserUUID(String userUUID, Pageable pageable) {
        List<UserAuctionParticipateResDto> content = queryFactory
                .select(new QUserAuctionParticipateResDto(
                        auction.user.profileImgUrl,
                        auction.user.nickname,
                        auction.createdTime,
                        auction.currentHighestBidAmount,
                        auction.bidCount
                ))
                .from(auction)
                .where(vipUserUUIDEq(userUUID) , auction.auctionType.eq(AuctionType.PROGRESS))
                .orderBy(auction.createdTime.asc())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(auction.count())
                .from(auction)
                .where(vipUserUUIDEq(userUUID),auction.auctionType.eq(AuctionType.PROGRESS));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    @Override
    public Page<UserAuctionInvalidityResDto> findAllVipInvalidityByUserUUID(String userUUID, Pageable pageable) {
        List<UserAuctionInvalidityResDto> content = queryFactory
                .select(new QUserAuctionInvalidityResDto(
                        auction.user.profileImgUrl,
                        auction.user.nickname,
                        auction.createdTime
                ))
                .from(auction)
                .where(vipUserUUIDEq(userUUID), auction.auctionType.eq(AuctionType.INVALIDITY))
                .orderBy(auction.createdTime.desc())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(auction.count())
                .from(auction)
                .where(vipUserUUIDEq(userUUID),auction.auctionType.eq(AuctionType.INVALIDITY));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }
    @Override
    public Page<ProgressAuctionResDto> findProgressGeneralAuction(Pageable pageable, AuctionSortType sortType, String search) {
        BooleanExpression searchCondition = search != null ? auction.user.nickname.contains(search)
                : Expressions.asBoolean(true).isTrue();

        List<ProgressAuctionResDto> content = queryFactory
                .select(new QProgressAuctionResDto(
                        auction.user.profileImgUrl,
                        auction.user.nickname,
                        auction.auctionUUID,
                        auction.createdTime,
                        auction.currentHighestBidAmount,
                        auction.bidCount
                ))
                .from(auction)
                .where(auction.auctionType.eq(AuctionType.PROGRESS).and(auction.user.isWithdrawn.eq(false)).and(searchCondition))
                .orderBy(orderType(sortType))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(auction.count())
                .from(auction)
                .where(auction.auctionType.eq(AuctionType.PROGRESS).and(auction.user.isWithdrawn.eq(false)).and(searchCondition));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    private OrderSpecifier<?> orderType(AuctionSortType sortType) {
        if (sortType == null) {
            return new OrderSpecifier<>(Order.ASC, auction.createdTime);
        }
        return switch (sortType) {
            case CREATE_ASC -> new OrderSpecifier<>(Order.ASC, auction.createdTime);
            case CREATE_DESC -> new OrderSpecifier<>(Order.DESC, auction.createdTime);
            case POPULAR -> new OrderSpecifier<>(Order.DESC, auction.bidCount);
        };
    }
    private BooleanExpression currentHighestBidderIdEq(String userUUID){
        return userUUID != null ? auction.currentHighestBidderUUID.eq(userUUID) : null;
    }

    private BooleanExpression vipUserUUIDEq(String userUUID){
        return userUUID != null ? auction.user.userUUID.eq(userUUID) : null;
    }

    private BooleanExpression meetingDateBefore(){
        //x>=y 만남시간이 현재보다 뒤인경우 - 아직 안만난경우
        return auction.meetingDate.goe(LocalDateTime.now());
    }

    private BooleanExpression meetingDateAfter(){
        //x<y 만남후
        return auction.meetingDate.lt(LocalDateTime.now());
    }
}
