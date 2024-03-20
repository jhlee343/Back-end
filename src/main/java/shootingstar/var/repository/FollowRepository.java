package shootingstar.var.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shootingstar.var.entity.Follow;

import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long>, FollowRepositoryCustom {

//    Optional<Follow> findByFollowingId(Long followingId);
    Optional<Follow> findByFollowUUID(String followUUID);
}
