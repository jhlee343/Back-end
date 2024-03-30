package shootingstar.var.repository.vip;

import org.springframework.data.jpa.repository.JpaRepository;
import shootingstar.var.entity.User;
import shootingstar.var.entity.VipInfo;
import java.util.Optional;

public interface VipInfoRepository extends JpaRepository<VipInfo,Long> {
    Optional<VipInfo> findVipInfoByUser(User user);

}
