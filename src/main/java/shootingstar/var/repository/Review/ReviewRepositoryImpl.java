package shootingstar.var.repository.Review;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import shootingstar.var.dto.res.QUserReceiveReviewDto;
import shootingstar.var.dto.res.UserReceiveReviewDto;

import java.util.List;
import static shootingstar.var.entity.QReview.review;

public class ReviewRepositoryImpl implements ReviewRepositoryCustom{
    private final JPAQueryFactory queryFactory;
    public ReviewRepositoryImpl(EntityManager em){
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<UserReceiveReviewDto> findAllByserUUID(String userUUID) {
                return queryFactory
                .select(new QUserReceiveReviewDto(
                        review.reviewUUID,
                        review.ticketId.ticketUUID,
                        review.reviewContent,
                        review.reviewRating,
                        review.writerId.userUUID
                ))
                .from(review)
                .where(userEqReceiver(userUUID))
                .fetch();
    }

    @Override
    public Page<UserReceiveReviewDto> findAllReviewByuserUUID(String userUUID, Pageable pageable) {
        List<UserReceiveReviewDto> content = queryFactory
                .select(new QUserReceiveReviewDto(
                        review.reviewUUID,
                        review.ticketId.ticketUUID,
                        review.reviewContent,
                        review.reviewRating,
                        review.writerId.userUUID
                ))
                .from(review)
                .where()
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy()
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(review.count())
                .from(review);

        return PageableExecutionUtils.getPage(content,pageable,countQuery::fetchOne);
    }

    private BooleanExpression userEqReceiver(String userUUID){
        return userUUID !=null ? review.receiverId.userUUID.eq(userUUID) : null;
    }
}
