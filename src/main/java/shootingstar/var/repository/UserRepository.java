package shootingstar.var.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import shootingstar.var.entity.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustom {
    Optional<User> findByKakaoId(String kakaoId);

    Optional<User> findByUserUUID(UUID uuid);
    boolean existsByNickname(String nickname);
}
