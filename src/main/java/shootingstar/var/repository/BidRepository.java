package shootingstar.var.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import shootingstar.var.entity.Bid;

public interface BidRepository extends JpaRepository<Bid, Long> {
    List<Bid> findTop10ByAuction_AuctionIdOrderByCreatedTimeDesc(Long auctionId);
}
