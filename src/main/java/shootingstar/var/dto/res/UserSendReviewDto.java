package shootingstar.var.dto.res;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

import java.time.LocalDateTime;

//작성 리뷰 불러오기
@Data
public class UserSendReviewDto {
    private String reviewUUID;
    private String ticketUUID;

    private String reviewContent;
    private Integer reviewRating;

    private String receiverUUID;
    private String receiverNickname;
    private String receiverImgUrl;

    private String vipNickname;

    private LocalDateTime meetingDate;
    private String meetingLocation;
    private long highestBidAmount;
    //내가 보낸 사람 uuid

    @QueryProjection
    public UserSendReviewDto(String reviewUUID, String ticketUUID, String reviewContent, Integer reviewRating,
                             String receiverUUID, String receiverNickname, String receiverImgUrl, String vipNickname, LocalDateTime meetingDate, String meetingLocation, long highestBidAmount){
        this.reviewUUID = reviewUUID;
        this.ticketUUID = ticketUUID;
        this.reviewContent = reviewContent;
        this.reviewRating = reviewRating;
        this.receiverUUID = receiverUUID;
        this.receiverNickname = receiverNickname;
        this.receiverImgUrl = receiverImgUrl;
        this.vipNickname = vipNickname;
        this.meetingDate = meetingDate;
        this.meetingLocation = meetingLocation;
        this.highestBidAmount = highestBidAmount;
    }

}
