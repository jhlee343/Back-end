package shootingstar.var.dto.res;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import shootingstar.var.entity.Ticket;
import shootingstar.var.entity.User;

//작성 리뷰 불러오기
@Data
public class UserSendReviewDto {
    private String reviewUUID;
    private Ticket ticket;
    private String reviewContent;
    private Double reviewRating;
    private User receiver;
    //내가 보낸 사람 uuid
    @QueryProjection
    public UserSendReviewDto(String reviewUUID, Ticket ticket, String reviewContent,
                             Double reviewRating, User receiver ){
        this.reviewUUID = reviewUUID;
        this.ticket = ticket;
        this.reviewContent = reviewContent;
        this.reviewRating = reviewRating;
        this.receiver = receiver;
    }

}
