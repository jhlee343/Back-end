package shootingstar.var.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import shootingstar.var.dto.res.UserAuctionSuccessList;

import java.util.List;

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
}
