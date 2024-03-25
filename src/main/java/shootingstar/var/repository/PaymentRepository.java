package shootingstar.var.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shootingstar.var.entity.PaymentsInfo;

public interface PaymentRepository extends JpaRepository<PaymentsInfo, Long> {
}
