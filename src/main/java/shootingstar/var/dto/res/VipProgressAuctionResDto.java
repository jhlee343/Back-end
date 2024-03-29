package shootingstar.var.dto.res;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class VipProgressAuctionResDto {
    private String profileImgUrl;
    private String auctionUUID;
    private LocalDateTime createdTime;
    private Long currentHighestBidAmount;
    private Long bidCount;

    @QueryProjection

    public VipProgressAuctionResDto(String profileImgUrl, String auctionUUID, LocalDateTime createdTime, Long currentHighestBidAmount, Long bidCount) {
        this.profileImgUrl = profileImgUrl;
        this.auctionUUID = auctionUUID;
        this.createdTime = createdTime;
        this.currentHighestBidAmount = currentHighestBidAmount;
        this.bidCount = bidCount;
    }
}
