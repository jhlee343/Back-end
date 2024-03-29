package shootingstar.var.repository.warning;

import shootingstar.var.dto.req.WarningListDto;

import java.util.List;

public interface WarningRepositoryCustom {
    List<WarningListDto> findAllWarnByUserUUID(String userUUID);

//    List<WarningListDto> findAllWarnByUserId(UUID userId);
}
