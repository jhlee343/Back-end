package shootingstar.var.repository.Warning;

import org.springframework.data.jpa.repository.JpaRepository;
import shootingstar.var.dto.req.WarningListDto;
import shootingstar.var.entity.Warning;

import java.util.List;
import java.util.UUID;

public interface WarningRepository extends JpaRepository<Warning, Long>, WarningRepositoryCustom {

}
