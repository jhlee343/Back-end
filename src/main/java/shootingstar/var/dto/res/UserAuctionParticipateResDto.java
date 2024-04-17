package shootingstar.var.dto.res;

import com.querydsl.core.annotations.QueryProjection;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserAuctionParticipateResDto {
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

    @NotNull
    private String auctionUUID;

    @QueryProjection
    public UserAuctionParticipateResDto(String profileImgUrl, String vipUserName, LocalDateTime auctionCreatedDate,
                                      long bidCount, long currentHighestBidAmount, String auctionUUID){
        this.profileImgUrl = profileImgUrl;
        this.vipUserName = vipUserName;
        this.auctionCreatedDate = auctionCreatedDate;
        this.bidCount = bidCount;
        this.currentHighestBidAmount = currentHighestBidAmount;
        this.auctionUUID = auctionUUID;
    }
}
