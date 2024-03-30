package shootingstar.var.repository.banner;

import shootingstar.var.dto.res.GetBannerResDto;

import java.util.List;

public interface BannerRepositoryCustom {
    List<GetBannerResDto> findAllBanner();
}
