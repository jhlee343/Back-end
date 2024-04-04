package shootingstar.var.repository.vip;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import shootingstar.var.dto.res.QAllVipInfosDto;
import shootingstar.var.dto.res.AllVipInfosDto;

import java.util.List;

import static org.springframework.util.StringUtils.hasText;
import static shootingstar.var.entity.QVipInfo.vipInfo;
import static shootingstar.var.entity.VipApprovalType.STANDBY;

public class VipInfoRepositoryImpl implements VipInfoRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    public VipInfoRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<AllVipInfosDto> findAllVipInfos(String search, Pageable pageable) {
        List<AllVipInfosDto> content = queryFactory
                .select(new QAllVipInfosDto(
                        vipInfo.vipInfoUUID,
                        vipInfo.vipName,
                        vipInfo.vipJob,
                        vipInfo.vipCareer,
                        vipInfo.vipIntroduce
                ))
                .from(vipInfo)
                .where(vipInfo.vipApproval.eq(STANDBY), checkSearch(search))
                .orderBy(vipInfo.vipInfoId.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(vipInfo.count())
                .from(vipInfo)
                .where(vipInfo.vipApproval.eq(STANDBY), checkSearch(search));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    private BooleanExpression checkSearch (String search) {
        if (!hasText(search)) {
            return null;
        }

        return vipInfo.vipName.eq(search);
    }
}