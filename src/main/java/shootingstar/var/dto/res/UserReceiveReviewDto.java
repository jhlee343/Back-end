package shootingstar.var.dto.res;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

//받은 리뷰 불러오기
@Data
public class UserReceiveReviewDto {
    private String reviewUUID;
    private String ticketUUID;
    private String reviewContent;
    private Double reviewRating;
    private String writerId;
    //나에게 보낸사람 uuid
    @QueryProjection
    public UserReceiveReviewDto(String reviewUUID, String ticketUUID, String reviewContent
    ,Double reviewRating, String writerId){
        this.reviewUUID = reviewUUID;
        this.ticketUUID = ticketUUID;
        this.reviewContent = reviewContent;
        this.reviewRating = reviewRating;
        this.writerId = writerId;
    }
}
