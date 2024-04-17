package shootingstar.var.repository.admin;

import org.springframework.data.jpa.repository.JpaRepository;
import shootingstar.var.entity.Admin;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    Optional<Admin> findByAdminLoginId(String loginId);
    Optional<Admin> findByAdminUUID(String uuid);

    boolean existsByAdminLoginId(String loginId);

    boolean existsByNickname(String nickname);
}
