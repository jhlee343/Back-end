package shootingstar.var.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Min;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Bid extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bidId;

    private String bidUUID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auctionId")
    private Auction auction;

    private String bidderNickname;

    @Min(value = 100000)
    private long bidAmount;

    @Builder
    public Bid(Auction auction, String bidderNickname, long bidAmount) {
        this.bidUUID = UUID.randomUUID().toString();
        this.auction = auction;
        this.bidderNickname = bidderNickname;
        this.bidAmount = bidAmount;
    }
}
