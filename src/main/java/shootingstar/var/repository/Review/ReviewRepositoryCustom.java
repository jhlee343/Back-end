package shootingstar.var.repository.Review;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import shootingstar.var.dto.res.UserReceiveReviewDto;
import shootingstar.var.dto.res.UserSendReviewDto;

import java.util.List;

public interface ReviewRepositoryCustom {
    List<UserReceiveReviewDto> findReceiveByUserUUID(String userUUID);
    Page<UserReceiveReviewDto> findAllReceiveByUserUUID(String userUUID, Pageable pageable);

    List<UserSendReviewDto> findSendByUserUUID(String userUUID);
    Page<UserSendReviewDto> findAllSendByUserUUID(String userUUID, Pageable pageable);


}
