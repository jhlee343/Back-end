package shootingstar.var.repository.payment;

import org.springframework.data.jpa.repository.JpaRepository;
import shootingstar.var.entity.PaymentsInfo;

public interface PaymentRepository extends JpaRepository<PaymentsInfo, Long> {
}
