package shootingstar.var.dto.res;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
public class BidLog {
    private String bidderNickname;
    private long bidAmount;
    private LocalDateTime participatedBidTime;

    @Builder
    public BidLog(String bidderNickname, long bidAmount, LocalDateTime participatedBidTime) {
        this.bidderNickname = bidderNickname;
        this.bidAmount = bidAmount;
        this.participatedBidTime = participatedBidTime;
    }
}
