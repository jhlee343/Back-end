package shootingstar.var.repository;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import shootingstar.var.dto.res.QUserAuctionParticipateList;
import shootingstar.var.dto.res.UserAuctionParticipateList;
import shootingstar.var.dto.res.UserAuctionSuccessList;
import shootingstar.var.dto.res.QUserAuctionSuccessList;
import shootingstar.var.enums.type.AuctionType;

import java.time.LocalDateTime;
import java.util.List;
import static shootingstar.var.entity.QAuction.auction;
import static shootingstar.var.entity.QUser.user;
import static shootingstar.var.entity.ticket.QTicket.ticket;

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
                        auction.user.name,
                        auction.meetingDate,
                        user.nickname
                ))
                .from(auction)
                .where(currentHighestBidderIdEq(userUUID),auction.auctionType.eq(AuctionType.SUCCESS))
                .orderBy(auction.meetingDate.desc())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(auction.count())
                .from(auction)
                .where(currentHighestBidderIdEq(userUUID),auction.auctionType.eq(AuctionType.SUCCESS));


    //만남 전 만남후 api 두개 만들어야함
    @Override
    public Page<UserAuctionSuccessResDto> findAllSuccessAfterByUserUUID(String userUUID, Pageable pageable) {
        List<UserAuctionSuccessResDto> content = queryFactory
                .select(new QUserAuctionSuccessResDto(
                        auction.user.profileImgUrl,
                        auction.user.name,
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
    public Page<UserAuctionSuccessResDto> findAllVipSuccessByUserUUID(String userUUID, Pageable pageable) {
        List<UserAuctionSuccessResDto> content = queryFactory
                .select(new QUserAuctionSuccessResDto(
                        auction.user.profileImgUrl,
                        auction.user.nickname,
                        auction.createdTime,
                        auction.currentHighestBidderUUID
                ))
                .from(auction)
                .where(auction.auctionType.eq(AuctionType.SUCCESS))
                .orderBy(auction.meetingDate.asc())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(auction.count())
                .from(auction)
                .where(vipUserUUIDEq(userUUID),auction.auctionType.eq(AuctionType.SUCCESS));

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
                .orderBy(auction.createdTime.asc())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(auction.count())
                .from(auction)
                .where(vipUserUUIDEq(userUUID),auction.auctionType.eq(AuctionType.INVALIDITY));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    private BooleanExpression currentHighestBidderIdEq(String userUUID){
        return userUUID != null ? auction.currentHighestBidderUUID.eq(userUUID) : null;
    }

    private BooleanExpression vipUserUUIDEq(String userUUID){
        return userUUID != null ? auction.user.userUUID.eq(userUUID) : null;
    }

}
