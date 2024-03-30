package shootingstar.var.repository.user;

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
                        vipInfo.user.userUUID,
                        vipInfo.user.profileImgUrl,
                        vipInfo.user.nickname,
                        vipInfo.user.rating,
                        vipInfo.vipJob,
                        vipInfo.vipCareer,
                        vipInfo.vipIntroduce
                ))
                .from(vipInfo)
                .where(vipInfo.user.userUUID.eq(vipUUID))
                .fetchOne();

        List<VipProgressAuctionResDto> progressAuctionList = queryFactory
                .select(new QVipProgressAuctionResDto(
                        auction.user.profileImgUrl,
                        auction.auctionUUID,
                        auction.createdTime,
                        auction.currentHighestBidAmount,
                        auction.bidCount
                ))
                .from(auction)
                .where(auction.user.userUUID.eq(vipUUID)
                        .and(auction.auctionType.eq(AuctionType.PROGRESS)))
                .fetch();

        if (vipDetailResDto != null) {
            vipDetailResDto.setProgressAuctionList(progressAuctionList);
        }

        List<VipReceiveReviewResDto> receiveReviewList = queryFactory
                .select(new QVipReceiveReviewResDto(
                        review.writer.nickname,
                        review.reviewRating,
                        review.reviewContent
                ))
                .from(review)
                .where(review.receiver.userUUID.eq(vipUUID))
                .fetch();

        if (vipDetailResDto != null) {
            vipDetailResDto.setReceiveReviewList(receiveReviewList);
        }

        return vipDetailResDto;
    }
}
