package shootingstar.var.repository.Review;

import org.springframework.data.jpa.repository.JpaRepository;
import shootingstar.var.entity.Review;

public interface ReviewRepository extends JpaRepository<Review,Long>, ReviewRepositoryCustom {
}
