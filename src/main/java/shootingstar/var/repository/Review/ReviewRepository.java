package shootingstar.var.repository.Review;

import org.springframework.data.jpa.repository.JpaRepository;
import shootingstar.var.entity.Review;

import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review,Long>, ReviewRepositoryCustom {

    Optional<Review> findByReviewId(Long reviewId);
}
