package shootingstar.var.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shootingstar.var.entity.auction.AuctionReport;

public interface AuctionReportRepository extends JpaRepository<AuctionReport, Long> {
}
