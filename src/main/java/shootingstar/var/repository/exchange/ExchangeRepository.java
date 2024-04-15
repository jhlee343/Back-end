package shootingstar.var.repository.exchange;

import org.springframework.data.jpa.repository.JpaRepository;
import shootingstar.var.entity.Exchange;

import java.util.Optional;

public interface ExchangeRepository extends JpaRepository<Exchange, Long>, ExchangeRepositoryCustom {
    Optional<Exchange> findByExchangeUUID(String exchangeUUID);
}
