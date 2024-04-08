package shootingstar.var.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shootingstar.var.entity.Bid;

public interface BidRepository extends JpaRepository<Bid, Long> {
}
