package shootingstar.var.repository.user;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
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

        List<VipProgressAuctionResDto> progressAuctionList = getProgressAutionList(vipUUID, 0L, 3);

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
    public Page<VipProgressAuctionResDto> findVipProgressAuction(String vipUUID, Pageable pageable) {
        List<VipProgressAuctionResDto> content = getProgressAutionList(vipUUID, pageable.getOffset(), pageable.getPageSize());

        JPAQuery<Long> countQuery = queryFactory.
                select(auction.count())
                .from(auction)
                .where(auction.user.userUUID.eq(vipUUID)
                        .and(auction.auctionType.eq(AuctionType.PROGRESS)));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }


    private List<VipProgressAuctionResDto> getProgressAutionList(String vipUUID, Long offset, Integer pageSize) {
        return queryFactory
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
}
