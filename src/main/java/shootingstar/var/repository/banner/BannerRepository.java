package shootingstar.var.repository.banner;

import org.springframework.data.jpa.repository.JpaRepository;
import shootingstar.var.entity.Banner;

public interface BannerRepository extends JpaRepository<Banner, Long>, BannerRepositoryCustom {
}
