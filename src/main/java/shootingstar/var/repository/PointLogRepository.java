package shootingstar.var.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shootingstar.var.entity.PointLog;

public interface PointLogRepository extends JpaRepository<PointLog, Long> {
}
