package shootingstar.var.repository.Vip;

import org.springframework.data.jpa.repository.JpaRepository;
import shootingstar.var.entity.VipInfo;
import java.util.Optional;

public interface VipInfoRepository extends JpaRepository<VipInfo,Long> {
    Optional<VipInfo> findByvipInfoUUID(String vipInfoUUID);
}
