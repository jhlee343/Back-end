package shootingstar.var.dto.res;

import com.querydsl.core.annotations.QueryProjection;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserAuctionParticipateList {
    @NotNull
    private String vipUserName;

    @NotNull
    private LocalDateTime auctionCreatedDate;

    @NotNull
    private long bidCount;

    @NotNull
    private long currentHighestBidAmount;

    @NotNull
    private String profileImgUrl;
    @QueryProjection
    public UserAuctionParticipateList(String profileImgUrl, String vipUserName, LocalDateTime auctionCreatedDate,
                                      long bidCount, long currentHighestBidAmount){
        this.profileImgUrl = profileImgUrl;
        this.vipUserName = vipUserName;
        this.auctionCreatedDate = auctionCreatedDate;
        this.bidCount = bidCount;
        this.currentHighestBidAmount = currentHighestBidAmount;
    }
}
