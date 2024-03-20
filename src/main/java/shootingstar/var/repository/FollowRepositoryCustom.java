package shootingstar.var.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import shootingstar.var.dto.req.FollowingDto;
import shootingstar.var.entity.Follow;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FollowRepositoryCustom {
    List<FollowingDto> findAllByFollowerId(String followerId);
 //   Optional<Follow> findByFollwerIdFollowingId(Long followerId,Long followingId);
}
