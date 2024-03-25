package shootingstar.var.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shootingstar.var.entity.Auction;
import shootingstar.var.entity.ExchangeForm;

import java.util.Optional;

public interface ExchangeFormRepository  extends JpaRepository<ExchangeForm, Long> {
}
