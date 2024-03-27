package shootingstar.var.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import shootingstar.var.dto.res.UserAuctionParticipateList;
import shootingstar.var.dto.res.UserAuctionSuccessList;
import shootingstar.var.dto.res.QUserAuctionSuccessList;
import shootingstar.var.dto.res.QUserAuctionParticipateList;
import shootingstar.var.entity.AuctionType;

import java.util.List;
import static shootingstar.var.entity.QAuction.auction;
import static shootingstar.var.entity.QReview.review;

public class AuctionRepositoryImpl implements AuctionRepositoryCustom{
    private final JPAQueryFactory queryFactory;
    public AuctionRepositoryImpl(EntityManager em){
        this.queryFactory = new JPAQueryFactory(em);
    }
    @Override
    public List<UserAuctionSuccessList> findSuccessList(String userUUID, Pageable pageable) {
        return null;
    }

    @Override
    public Page<UserAuctionSuccessList> findAllSuccessByuserUUID(String userUUID, Pageable pageable) {
        return null;
    }

    @Override
    public List<UserAuctionParticipateList> findParticipateList(String userUUID, Pageable pageable) {
        return null;
    }
    @Override
    public Page<UserAuctionParticipateList> findAllParticipateByuserUUID(String userUUID, Pageable pageable) {
        return null;
    }
    private BooleanExpression basicUserEq(String userUUID){
        return userUUID != null ? auction.currentHighestBidderId.eq(userUUID) : null;
    }
}
