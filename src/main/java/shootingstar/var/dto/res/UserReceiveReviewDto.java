package shootingstar.var.dto.res;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import shootingstar.var.entity.Ticket;
import shootingstar.var.entity.User;

import java.time.LocalDate;

//받은 리뷰 불러오기
@Data
public class UserReceiveReviewDto {
    private String reviewUUID;
    private String ticketUUID;
    private String reviewContent;
    private Double reviewRating;
    private String writerUUID;
    //나에게 보낸사람 uuid
    @QueryProjection
    public UserReceiveReviewDto(String reviewUUID, String ticketUUID, String reviewContent
    ,Double reviewRating, String writerUUID){
        this.reviewUUID = reviewUUID;
        this.ticketUUID = ticketUUID;
        this.reviewContent = reviewContent;
        this.reviewRating = reviewRating;
        this.writerUUID = writerUUID;
    }
}
