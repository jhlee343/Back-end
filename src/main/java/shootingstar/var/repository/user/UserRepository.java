package shootingstar.var.repository.user;


import org.springframework.data.jpa.repository.JpaRepository;
import shootingstar.var.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustom {
    Optional<User> findByKakaoIdAndIsWithdrawn(String kakaoId, Boolean isWithdrawn);

    Optional<User> findByUserUUID(String uuid);
    boolean existsByNicknameAndIsWithdrawn(String nickname, Boolean isWithdrawn);
    boolean existsByEmailAndIsWithdrawn(String email, Boolean isWithdrawn);
    Optional<User> findByNickname(String nickname);
    Optional<User> findByUserId(Long followingId);
}
