package shootingstar.var.repository;

import shootingstar.var.dto.res.VipDetailResDto;

public interface UserRepositoryCustom {
    VipDetailResDto findVipDetailByVipUUID(String vipUUID);
}
