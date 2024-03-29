package shootingstar.var.repository.warning;

import org.springframework.data.jpa.repository.JpaRepository;
import shootingstar.var.entity.Warning;

public interface WarningRepository extends JpaRepository<Warning, Long>, WarningRepositoryCustom {

}
