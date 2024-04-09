package shootingstar.var.repository.log;

import org.springframework.data.jpa.repository.JpaRepository;
import shootingstar.var.entity.log.PointLog;

public interface PointLogRepository extends JpaRepository<PointLog, Long> {
}
