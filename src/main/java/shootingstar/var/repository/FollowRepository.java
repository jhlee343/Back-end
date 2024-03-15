package shootingstar.var.repository;

import org.hibernate.type.descriptor.converter.spi.JpaAttributeConverter;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import shootingstar.var.Service.dto.FollowingDto;
import shootingstar.var.entity.Follow;

public interface FollowRepository extends JpaRepository<Follow, Long>, FollowRepositoryCustom {
}
