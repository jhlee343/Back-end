package shootingstar.var.repository.user;

import shootingstar.var.dto.res.VipDetailResDto;

public interface UserRepositoryCustom {
    VipDetailResDto findVipDetailByVipUUID(String vipUUID);
}
