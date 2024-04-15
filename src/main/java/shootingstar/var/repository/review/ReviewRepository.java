package shootingstar.var.repository.review;

import org.springframework.data.jpa.repository.JpaRepository;
import shootingstar.var.entity.Review;

import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review,Long>, ReviewRepositoryCustom {
    Optional<Review> findByReviewUUID(String reviewUUID);
}
