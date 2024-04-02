package shootingstar.var.dto.res;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProgressAuctionResDto {
    private String profileImgUrl;
    private String vipNickname;
    private String auctionUUID;
    private LocalDateTime createdTime;
    private Long currentHighestBidAmount;
    private Long bidCount;

    @QueryProjection
    public ProgressAuctionResDto(String profileImgUrl, String vipNickname, String auctionUUID, LocalDateTime createdTime, Long currentHighestBidAmount, Long bidCount) {
        this.profileImgUrl = profileImgUrl;
        this.vipNickname = vipNickname;
        this.auctionUUID = auctionUUID;
        this.createdTime = createdTime;
        this.currentHighestBidAmount = currentHighestBidAmount;
        this.bidCount = bidCount;
    }
}
