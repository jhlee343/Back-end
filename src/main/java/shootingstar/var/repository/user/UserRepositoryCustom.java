package shootingstar.var.repository.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import shootingstar.var.dto.res.VipDetailResDto;
import shootingstar.var.dto.res.VipProgressAuctionResDto;

public interface UserRepositoryCustom {
    VipDetailResDto findVipDetailByVipUUID(String vipUUID);

    Page<VipProgressAuctionResDto> findVipProgressAuction(String vipUUID, Pageable pageable);
}
