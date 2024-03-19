package shootingstar.var.repository.Warning;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import shootingstar.var.dto.req.QWarningListDto;
import shootingstar.var.dto.req.WarningListDto;

import java.util.List;
import java.util.UUID;

import static shootingstar.var.entity.QWarning.warning;
public class WarningRepositoryCustomImpl implements WarningRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    public WarningRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<WarningListDto> findAllWarnByUserId(UUID userId) {
         return queryFactory
                 .select(new QWarningListDto(
                         warning.warningUUID,
                         warning.userId,
                        warning.warningContent
                ))
            .from(warning)
                .where(IdEq(userId))
            .fetch();
}

    private BooleanExpression IdEq(UUID userId){
        return userId !=null ? warning.userId.userUUID.eq(userId) : null;
    }
}
