package shootingstar.var.repository;

import shootingstar.var.dto.req.FollowingDto;

import java.util.List;
import java.util.UUID;

public interface FollowRepositoryCustom {
    List<FollowingDto> findAllByFollowerId(UUID followerId);

}
