package shootingstar.var.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import shootingstar.var.Service.dto.FollowingDto;
import shootingstar.var.Service.dto.QFollowingDto;
import shootingstar.var.entity.Follow;
import shootingstar.var.entity.QFollow;

import java.util.List;
import java.util.Optional;

import static shootingstar.var.entity.QUser.user;
import static shootingstar.var.entity.QFollow.follow;
public class FollowRepositoryCustomImpl implements FollowRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    public FollowRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<FollowingDto> findAllByFollowerId(Long followerId) {
        return queryFactory
                .select(new QFollowingDto(
                        user.nickname,
                        user.profileImgUrl,
                        user.userId
                ))
                .from(follow)
                .where(IdEq(followerId))
                .fetch();
    }

    private BooleanExpression IdEq(Long followerId){
        return followerId !=null ? follow.user.userId.eq(followerId) : null;
    }
}
