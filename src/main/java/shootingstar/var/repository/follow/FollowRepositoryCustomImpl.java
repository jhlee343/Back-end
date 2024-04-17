package shootingstar.var.repository.follow;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import shootingstar.var.dto.req.QFollowingDto;
import shootingstar.var.dto.req.FollowingDto;


import java.util.List;
import java.util.UUID;

import static shootingstar.var.entity.QFollow.follow;
public class FollowRepositoryCustomImpl implements shootingstar.var.repository.follow.FollowRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    public FollowRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<FollowingDto> findAllByFollowerId(String followerId) {
        return queryFactory
                .select(new QFollowingDto(
                        follow.following.nickname,
                        follow.following.profileImgUrl,
                        follow.following.userUUID,
                        follow.followUUID
                ))
                .from(follow)
                .where(IdEq(followerId))
                .fetch();
    }

    private BooleanExpression IdEq(String followerId){
        return followerId !=null ? follow.follower.userUUID.eq(followerId) : null;
    }
}
