package shootingstar.var.dto.res;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

@Data
public class VipReceiveReviewResDto {
    private String userNickname;
    private double reviewRating;
    private String reviewContent;

    @QueryProjection

    public VipReceiveReviewResDto(String userNickname, double reviewRating, String reviewContent) {
        this.userNickname = userNickname;
        this.reviewRating = reviewRating;
        this.reviewContent = reviewContent;
    }
}
