package shootingstar.var.repository.Warning;

import shootingstar.var.dto.req.WarningListDto;

import java.util.List;
import java.util.UUID;

public interface WarningRepositoryCustom {
    List<WarningListDto> findAllWarnByUserId(UUID userUUID);
}
