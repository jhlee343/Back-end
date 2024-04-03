package shootingstar.var.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shootingstar.var.entity.Ban;

public interface BanRepository extends JpaRepository<Ban, Long> {
}
