package shootingstar.var.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shootingstar.var.entity.Auction;

public interface AuctionRepository extends JpaRepository<Auction, Long> {
}
