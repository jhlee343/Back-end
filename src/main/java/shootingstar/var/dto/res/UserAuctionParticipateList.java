package shootingstar.var.dto.res;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserAuctionParticipateList {
    private String vipUserName;
    private LocalDateTime auctionStartDate;
    private long bidCount;
    private long currentHighestBidAmount;
}
