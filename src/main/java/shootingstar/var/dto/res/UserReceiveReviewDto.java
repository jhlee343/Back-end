package shootingstar.var.dto.res;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.time.LocalDateTime;

//받은 리뷰 불러오기
@Data
public class UserReceiveReviewDto {
    private String reviewUUID;
    private String ticketUUID;

    private String reviewContent;
    private Integer reviewRating;

    private String writerUUID;
    private String writerNickname;
    private String writerImgUrl;

    private String vipNickname;

    private LocalDateTime meetingDate;
    private String meetingLocation;
    private long highestBidAmount;

    //나에게 보낸사람 uuid
    @QueryProjection
    public UserReceiveReviewDto(String reviewUUID, String ticketUUID, String reviewContent, Integer reviewRating,
                                String writerUUID,String writerNickname, String writerImgUrl, String vipNickname, LocalDateTime meetingDate, String meetingLocation, long highestBidAmount){
        this.reviewUUID = reviewUUID;
        this.ticketUUID = ticketUUID;
        this.reviewContent = reviewContent;
        this.reviewRating = reviewRating;
        this.writerUUID = writerUUID;
        this.writerNickname = writerNickname;
        this.writerImgUrl = writerImgUrl;
        this.vipNickname = vipNickname;
        this.meetingDate = meetingDate;
        this.meetingLocation = meetingLocation;
        this.highestBidAmount = highestBidAmount;
    }
}
