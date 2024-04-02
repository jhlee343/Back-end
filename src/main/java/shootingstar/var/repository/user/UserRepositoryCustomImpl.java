package shootingstar.var.repository.user;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import shootingstar.var.dto.res.*;
import shootingstar.var.enums.type.AuctionType;
import shootingstar.var.enums.type.UserType;

import java.util.List;

import static shootingstar.var.entity.QAuction.auction;
import static shootingstar.var.entity.QFollow.follow;
import static shootingstar.var.entity.QReview.review;
import static shootingstar.var.entity.QUser.user;
import static shootingstar.var.entity.QVipInfo.vipInfo;

public class UserRepositoryCustomImpl implements UserRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    public UserRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<VipListResDto> findVipList(Pageable pageable, String search, String userUUID) {
        BooleanExpression searchCondition = search != null ? user.nickname.contains(search) : Expressions.asBoolean(true).isTrue();
        BooleanExpression vipCondition = user.userType.eq(UserType.ROLE_VIP)
                .and(user.isWithdrawn.eq(false));

        List<VipListResDto> content = queryFactory
                .select(new QVipListResDto(
                        user.userUUID,
                        user.profileImgUrl,
                        user.nickname,
                        user.rating,
                        getIsFollow(userUUID)
                ))
                .from(user)
                .where(vipCondition.and(searchCondition))
                .orderBy(user.nickname.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(user.count())
                .from(user)
                .where(vipCondition.and(searchCondition));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    @Override
    public VipDetailResDto findVipDetailByVipUUID(String vipUUID, String userUUID) {
        VipDetailResDto vipDetailResDto = queryFactory
                .select(new QVipDetailResDto(
                        vipInfo.user.userUUID,
                        vipInfo.user.profileImgUrl,
                        vipInfo.user.nickname,
                        vipInfo.user.rating,
                        vipInfo.vipJob,
                        vipInfo.vipCareer,
                        vipInfo.vipIntroduce,
                        getIsFollow(userUUID)
                ))
                .from(vipInfo)
                .where(vipInfo.user.userUUID.eq(vipUUID))
                .fetchOne();

        List<ProgressAuctionResDto> progressAuctionList = getProgressAutionList(vipUUID, 0L, 3);

        if (vipDetailResDto != null) {
            vipDetailResDto.setProgressAuctionList(progressAuctionList);
        }

        List<VipReceiveReviewResDto> receiveReviewList = getReceiveReviewList(vipUUID, 0L, 3);

        if (vipDetailResDto != null) {
            vipDetailResDto.setReceiveReviewList(receiveReviewList);
        }

        return vipDetailResDto;
    }

    @Override
    public Page<ProgressAuctionResDto> findVipProgressAuction(String vipUUID, Pageable pageable) {
        List<ProgressAuctionResDto> content = getProgressAutionList(vipUUID, pageable.getOffset(), pageable.getPageSize());

        JPAQuery<Long> countQuery = queryFactory.
                select(auction.count())
                .from(auction)
                .where(auction.user.userUUID.eq(vipUUID)
                        .and(auction.auctionType.eq(AuctionType.PROGRESS)));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    @Override
    public Page<VipReceiveReviewResDto> findVipReceivedReview(String vipUUID, Pageable pageable) {
        List<VipReceiveReviewResDto> content = getReceiveReviewList(vipUUID, pageable.getOffset(), pageable.getPageSize());

        JPAQuery<Long> countQuery = queryFactory.
                select(review.count())
                .from(review)
                .where(review.receiver.userUUID.eq(vipUUID));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }


    private List<ProgressAuctionResDto> getProgressAutionList(String vipUUID, Long offset, Integer pageSize) {
        return queryFactory
                .select(new QProgressAuctionResDto(
                        auction.user.profileImgUrl,
                        auction.user.nickname,
                        auction.auctionUUID,
                        auction.createdTime,
                        auction.currentHighestBidAmount,
                        auction.bidCount
                ))
                .from(auction)
                .where(auction.user.userUUID.eq(vipUUID)
                        .and(auction.auctionType.eq(AuctionType.PROGRESS)))
                .orderBy(auction.createdTime.desc())
                .offset(offset)
                .limit(pageSize)
                .fetch();
    }

    private List<VipReceiveReviewResDto> getReceiveReviewList(String vipUUID, Long offset, Integer pageSize) {
        return queryFactory
                .select(new QVipReceiveReviewResDto(
                        review.writer.nickname,
                        review.reviewRating,
                        review.reviewContent
                ))
                .from(review)
                .where(review.receiver.userUUID.eq(vipUUID))
                .orderBy(review.createdTime.desc())
                .offset(offset)
                .limit(pageSize)
                .fetch();
    }

    private BooleanExpression getIsFollow(String userUUID) {
        return userUUID != null ?
                JPAExpressions
                        .select(follow.followId.count())
                        .from(follow)
                        .where(follow.follower.userUUID.eq(userUUID)
                                .and(follow.following.userUUID.eq(user.userUUID)))
                        .gt(0L) : Expressions.asBoolean(false).isTrue();
    }
}
