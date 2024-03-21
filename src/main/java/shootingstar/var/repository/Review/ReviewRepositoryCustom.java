package shootingstar.var.repository.Review;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import shootingstar.var.dto.res.UserReceiveReviewDto;

import java.util.List;

public interface ReviewRepositoryCustom {
    List<UserReceiveReviewDto> findAllByserUUID(String userUUID);
    Page<UserReceiveReviewDto> findAllReviewByuserUUID(String userUUID, Pageable pageable);
}
