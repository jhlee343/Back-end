package shootingstar.var.dto.res;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
public class BidInfoResDto {
    private long currentHighestBidAmount;
    private List<BidLog> bidLogs;

    @Builder
    public BidInfoResDto(long currentHighestBidAmount, List<BidLog> bidLogs) {
        this.currentHighestBidAmount = currentHighestBidAmount;
        this.bidLogs = bidLogs;
    }
}
