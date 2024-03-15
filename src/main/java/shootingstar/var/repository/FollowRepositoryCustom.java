package shootingstar.var.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import shootingstar.var.Service.dto.FollowingDto;

import java.util.List;

public interface FollowRepositoryCustom {
    List<FollowingDto> findByFollowerId(Long followerId);
}
