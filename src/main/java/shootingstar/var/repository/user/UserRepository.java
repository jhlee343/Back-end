package shootingstar.var.repository.user;


import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import shootingstar.var.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustom {
    Optional<User> findByKakaoIdAndIsWithdrawn(String kakaoId, Boolean isWithdrawn);

    Optional<User> findByUserUUID(String uuid);
    boolean existsByNicknameAndIsWithdrawn(String nickname, Boolean isWithdrawn);
    boolean existsByEmailAndIsWithdrawn(String email, Boolean isWithdrawn);
    Optional<User> findByNickname(String nickname);
    Optional<User> findByUserId(Long followingId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select u from User u where u.userUUID = :userUUID")
    Optional<User> findByUserUUIDWithPessimisticLock(String userUUID);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select u from User u where u.userId = :userId")
    Optional<User> findByUserIdWithPessimisticLock(Long userId);
}
