package shootingstar.var.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import shootingstar.var.dto.res.*;
import shootingstar.var.enums.type.AuctionType;

import java.util.List;

import static shootingstar.var.entity.QAuction.auction;
import static shootingstar.var.entity.QReview.review;
import static shootingstar.var.entity.QUser.user;
import static shootingstar.var.entity.QVipInfo.vipInfo;

public class UserRepositoryCustomImpl implements UserRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    public UserRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }


    @Override
    public VipDetailResDto findVipDetailByVipUUID(String vipUUID) {
        VipDetailResDto vipDetailResDto = queryFactory
                .select(new QVipDetailResDto(
                        user.userUUID,
                        user.profileImgUrl,
                        user.nickname,
                        user.rating,
                        vipInfo.vipJob,
                        vipInfo.vipCareer,
                        vipInfo.vipIntroduce
                ))
                .from(vipInfo)
                .leftJoin(vipInfo.user, user)
                .where(vipInfo.user.userUUID.eq(vipUUID))
                .fetchOne();

        List<VipProgressAuctionResDto> progressAuctionList = queryFactory
                .select(new QVipProgressAuctionResDto(
                        user.profileImgUrl,
                        auction.auctionUUID,
                        auction.createdTime,
                        auction.currentHighestBidAmount,
                        auction.bidCount
                ))
                .from(auction)
                .leftJoin(auction.user, user)
                .where(auction.user.userUUID.eq(vipUUID)
                        .and(auction.auctionType.eq(AuctionType.PROGRESS)))
                .fetch();

        if (vipDetailResDto != null) {
            vipDetailResDto.setProgressAuctionList(progressAuctionList);
        }

        List<VipReceiveReviewResDto> receiveReviewList = queryFactory
                .select(new QVipReceiveReviewResDto(
                        user.nickname,
                        review.reviewRating,
                        review.reviewContent
                ))
                .from(review)
                .leftJoin(review.writer, user)
                .where(review.receiver.userUUID.eq(vipUUID))
                .fetch();

        if (vipDetailResDto != null) {
            vipDetailResDto.setReceiveReviewList(receiveReviewList);
        }

        return vipDetailResDto;
    }
}
