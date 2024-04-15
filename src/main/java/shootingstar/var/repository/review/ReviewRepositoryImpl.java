package shootingstar.var.repository.review;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import shootingstar.var.dto.res.*;

import java.util.List;

import static org.springframework.util.StringUtils.hasText;
import static shootingstar.var.entity.QReview.review;

public class ReviewRepositoryImpl implements ReviewRepositoryCustom{
    private final JPAQueryFactory queryFactory;
    public ReviewRepositoryImpl(EntityManager em){
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<UserReceiveReviewDto> findReceiveByUserUUID(String userUUID) {
                return queryFactory
                .select(new QUserReceiveReviewDto(
                        review.reviewUUID,
                        review.ticket.ticketUUID,
                        review.reviewContent,
                        review.reviewRating,
                        review.writer.nickname
                ))
                .from(review)
                .where(userEqReceiver(userUUID))
                .fetch();
    }

    @Override
    public Page<UserReceiveReviewDto> findAllReceiveByUserUUID(String userUUID, Pageable pageable) {
        List<UserReceiveReviewDto> content = queryFactory
                .select(new QUserReceiveReviewDto(
                        review.reviewUUID,
                        review.ticket.ticketUUID,
                        review.reviewContent,
                        review.reviewRating,
                        review.writer.nickname
                ))
                .from(review)
                .where(userEqReceiver(userUUID), review.receiver.isWithdrawn.eq(false))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(review.reviewId.desc())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(review.count())
                .from(review)
                .where(userEqReceiver(userUUID), review.receiver.isWithdrawn.eq(false));

        return PageableExecutionUtils.getPage(content,pageable,countQuery::fetchOne);
    }
    @Override
    public List<UserSendReviewDto> findSendByUserUUID(String userUUID) {
        return queryFactory
                .select(new QUserSendReviewDto(
                        review.reviewUUID,
                        review.ticket.ticketUUID,
                        review.reviewContent,
                        review.reviewRating,
                        review.receiver.nickname
                ))
                .from(review)
                .where(userEqWriter(userUUID))
                .fetch();
    }

    @Override
    public Page<UserSendReviewDto> findAllSendByUserUUID(String userUUID, Pageable pageable) {
        List<UserSendReviewDto> content = queryFactory
                .select(new QUserSendReviewDto(
                        review.reviewUUID,
                        review.ticket.ticketUUID,
                        review.reviewContent,
                        review.reviewRating,
                        review.receiver.nickname
                ))
                .from(review)
                .where(userEqWriter(userUUID), review.writer.isWithdrawn.eq(false))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(review.reviewId.desc())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(review.count())
                .from(review)
                .where(userEqReceiver(userUUID), review.writer.isWithdrawn.eq(true));

        return PageableExecutionUtils.getPage(content,pageable,countQuery::fetchOne);
    }

    @Override
    public Page<AllReviewsDto> findAllReviews(String search, Pageable pageable) {
        List<AllReviewsDto> content = queryFactory
                .select(new QAllReviewsDto(
                        review.reviewUUID,
                        review.writer.nickname,
                        review.receiver.nickname,
                        review.reviewContent,
                        review.createdTime,
                        review.isShowed
                ))
                .from(review)
                .where(checkSearch(search))
                .orderBy(review.reviewId.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(review.count())
                .from(review)
                .where(checkSearch(search));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    private BooleanExpression userEqReceiver(String userUUID){
        return userUUID !=null ? review.receiver.userUUID.eq(userUUID) : null;
    }

    private BooleanExpression userEqWriter(String userUUID){
        return userUUID != null ? review.writer.userUUID.eq(userUUID) : null;
    }

    private BooleanExpression checkSearch (String search) {
        if (!hasText(search)) {
            return null;
        }

        return review.writer.nickname.eq(search)
                .or(review.receiver.nickname.eq(search));
    }

}
