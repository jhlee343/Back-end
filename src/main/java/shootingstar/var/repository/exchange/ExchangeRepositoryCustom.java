package shootingstar.var.repository.exchange;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import shootingstar.var.dto.res.AllExchangesDto;

public interface ExchangeRepositoryCustom {
    Page<AllExchangesDto> findAllExchanges(String search, Pageable pageable);
}
