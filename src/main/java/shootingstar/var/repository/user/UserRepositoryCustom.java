package shootingstar.var.repository.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import shootingstar.var.dto.res.*;

public interface UserRepositoryCustom {
    VipDetailResDto findVipDetailByVipUUID(String vipUUID, String userUUID);

    Page<ProgressAuctionResDto> findVipProgressAuction(String vipUUID, Pageable pageable);
    Page<VipReceiveReviewResDto> findVipReceivedReview(String vipUUID, Pageable pageable);
    Page<VipListResDto> findVipList(Pageable pageable, String search, String userUUID);
    Page<AllUsersDto> findAllUsers(String search, Pageable pageable);
}
