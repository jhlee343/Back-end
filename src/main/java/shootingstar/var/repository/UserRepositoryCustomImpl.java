package shootingstar.var.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import shootingstar.var.dto.res.VipDetailResDto;

import java.util.Optional;

public class UserRepositoryCustomImpl implements UserRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    public UserRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }


    @Override
    public Optional<VipDetailResDto> findVipDetailByVipUUID(String vipUUID) {
        return Optional.empty();
    }
}
