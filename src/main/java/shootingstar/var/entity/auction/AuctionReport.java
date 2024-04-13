package shootingstar.var.entity.auction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shootingstar.var.entity.BaseTimeEntity;
import shootingstar.var.enums.status.AuctionReportStatus;

@Entity
@Getter
@NoArgsConstructor
public class AuctionReport extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long auctionReportId;

    private String auctionReportUUID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auction_id")
    private Auction auction;

    private String auctionReportNickname;

    @Column(columnDefinition = "LONGTEXT")
    private String auctionReportContent;

    @Enumerated(EnumType.STRING)
    private AuctionReportStatus auctionReportStatus;

    @Builder
    public AuctionReport(Auction auction, String auctionReportNickname, String auctionReportContent) {
        this.auctionReportUUID = UUID.randomUUID().toString();
        this.auction = auction;
        this.auctionReportNickname = auctionReportNickname;
        this.auctionReportContent = auctionReportContent;
        this.auctionReportStatus = AuctionReportStatus.STANDBY;
    }
}
