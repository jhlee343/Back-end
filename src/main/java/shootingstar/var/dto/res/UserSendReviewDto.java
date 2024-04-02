package shootingstar.var.dto.res;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

//작성 리뷰 불러오기
@Data
public class UserSendReviewDto {
    private String reviewUUID;
    private String ticketUUID;
    private String reviewContent;
    private Integer reviewRating;
    private String receiverId;
    //내가 보낸 사람 uuid
    @QueryProjection
    public UserSendReviewDto(String reviewUUID, String ticketUUID, String reviewContent,
                             Integer reviewRating, String receiverId ){
        this.reviewUUID = reviewUUID;
        this.ticketUUID = ticketUUID;
        this.reviewContent = reviewContent;
        this.reviewRating = reviewRating;
        this.receiverId = receiverId;
    }

}
