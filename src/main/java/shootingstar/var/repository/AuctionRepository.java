package shootingstar.var.repository;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import shootingstar.var.entity.Auction;

public interface AuctionRepository extends JpaRepository<Auction, Long> {
    Optional<Auction> findByAuctionUUID(UUID auctionUUID);
}
