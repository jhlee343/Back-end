package shootingstar.var.repository;

import shootingstar.var.dto.res.VipDetailResDto;

import java.util.Optional;

public interface UserRepositoryCustom {
    Optional<VipDetailResDto> findVipDetailByVipUUID(String vipUUID);
}
