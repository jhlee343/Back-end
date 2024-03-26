package shootingstar.var.repository.Review;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import shootingstar.var.dto.res.UserReceiveReviewDto;
import shootingstar.var.dto.res.UserSendReviewDto;

import java.util.List;

public interface ReviewRepositoryCustom {
    List<UserReceiveReviewDto> findReceiveByserUUID(String userUUID);
    Page<UserReceiveReviewDto> findAllReceiveByuserUUID(String userUUID, Pageable pageable);

    List<UserSendReviewDto> findSendByserUUID(String userUUID);
    Page<UserSendReviewDto> findAllSendByuserUUID(String userUUID, Pageable pageable);


}
