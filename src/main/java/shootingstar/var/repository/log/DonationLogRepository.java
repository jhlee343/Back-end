package shootingstar.var.repository.log;

import org.springframework.data.jpa.repository.JpaRepository;
import shootingstar.var.entity.log.DonationLog;

public interface DonationLogRepository extends JpaRepository<DonationLog, Long> {
}
