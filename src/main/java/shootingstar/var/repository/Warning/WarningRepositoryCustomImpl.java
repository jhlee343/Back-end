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
    public List<WarningListDto> findAllWarnByUserUUID(String userUUID) {
        return queryFactory
                .select(new QWarningListDto(
                        warning.warningUUID,
                        warning.user.userUUID,
                        warning.warningContent
                ))
                .from(warning)
                .where(IdEq(UUID.fromString(userUUID)))
                .fetch();
    }


    private BooleanExpression IdEq(UUID userUUID){
        return userUUID !=null ? warning.user.userUUID.eq(String.valueOf(userUUID)) : null;
    }
}
