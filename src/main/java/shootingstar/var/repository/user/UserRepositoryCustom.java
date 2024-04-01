package shootingstar.var.repository.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import shootingstar.var.dto.res.VipDetailResDto;
import shootingstar.var.dto.res.VipListResDto;
import shootingstar.var.dto.res.VipProgressAuctionResDto;
import shootingstar.var.dto.res.VipReceiveReviewResDto;

public interface UserRepositoryCustom {
    VipDetailResDto findVipDetailByVipUUID(String vipUUID);

    Page<VipProgressAuctionResDto> findVipProgressAuction(String vipUUID, Pageable pageable);
    Page<VipReceiveReviewResDto> findVipReceivedReview(String vipUUID, Pageable pageable);
    Page<VipListResDto> findVipList(Pageable pageable, String search, String userUUID);
}
