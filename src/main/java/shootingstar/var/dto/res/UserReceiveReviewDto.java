package shootingstar.var.dto.res;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import shootingstar.var.entity.Ticket;
import shootingstar.var.entity.User;

//받은 리뷰 불러오기
@Data
public class UserReceiveReviewDto {
    private String reviewUUID;
    private Ticket ticket;
    private String reviewContent;
    private Double reviewRating;
    private User sender;
    //나에게 보낸사람 uuid
    @QueryProjection
    public UserReceiveReviewDto(String reviewUUID, Ticket ticket, String reviewContent
    ,Double reviewRating, User sender){
        this.reviewUUID = reviewUUID;
        this.ticket = ticket;
        this.reviewContent = reviewContent;
        this.reviewRating = reviewRating;
        this.sender = sender;
    }
}
