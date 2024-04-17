package shootingstar.var.dto.res;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;

@Data
public class BidResDto {
    private String currentHighestBidderNickname;
    private long currentHighestBidAmount;
    private BigDecimal userPoint;

    @Builder
    public BidResDto(String currentHighestBidderNickname, long currentHighestBidAmount, BigDecimal userPoint) {
        this.currentHighestBidderNickname = currentHighestBidderNickname;
        this.currentHighestBidAmount = currentHighestBidAmount;
        this.userPoint = userPoint;
    }
}
