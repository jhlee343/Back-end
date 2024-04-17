package shootingstar.var.repository.vip;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import shootingstar.var.dto.res.AllVipInfosDto;

public interface VipInfoRepositoryCustom {
    Page<AllVipInfosDto> findAllVipInfos(String search, Pageable pageable);
}
