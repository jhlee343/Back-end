package shootingstar.var.dto.res;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AuctionDetailResDto {
    private String vipUUID;
    private String vipNickname;
    private String vipProfileImgUrl;
    private Double vipRating;
    private String auctionUUID;
    private LocalDateTime auctionCreatedTime;
    private LocalDateTime meetingDate;
    private String meetingLocation;
    private Long currentHighestBidAmount;
    private String meetingInfoText;
    private String meetingPromiseText;

    @Builder
    public AuctionDetailResDto(String vipUUID, String vipNickname, String vipProfileImgUrl, Double vipRating, String auctionUUID, LocalDateTime auctionCreatedTime, LocalDateTime meetingDate, String meetingLocation, Long currentHighestBidAmount, String meetingInfoText, String meetingPromiseText) {
        this.vipUUID = vipUUID;
        this.vipNickname = vipNickname;
        this.vipProfileImgUrl = vipProfileImgUrl;
        this.vipRating = vipRating;
        this.auctionUUID = auctionUUID;
        this.auctionCreatedTime = auctionCreatedTime;
        this.meetingDate = meetingDate;
        this.meetingLocation = meetingLocation;
        this.currentHighestBidAmount = currentHighestBidAmount;
        this.meetingInfoText = meetingInfoText;
        this.meetingPromiseText = meetingPromiseText;
    }
}
