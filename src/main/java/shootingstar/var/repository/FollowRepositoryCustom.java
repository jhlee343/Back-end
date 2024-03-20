package shootingstar.var.repository;

import shootingstar.var.dto.req.FollowingDto;

import java.util.List;

public interface FollowRepositoryCustom {
    List<FollowingDto> findAllByFollowerId(String followerId);
 //   Optional<Follow> findByFollwerIdFollowingId(Long followerId,Long followingId);
}
