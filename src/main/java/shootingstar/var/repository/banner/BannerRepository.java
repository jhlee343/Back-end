package shootingstar.var.repository.banner;

import org.springframework.data.jpa.repository.JpaRepository;
import shootingstar.var.entity.Banner;

import java.util.Optional;

public interface BannerRepository extends JpaRepository<Banner, Long>, BannerRepositoryCustom {
    Optional<Banner> findByBannerUUID(String bannerUUID);
}
