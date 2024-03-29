package shootingstar.var.dto.res;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

@Data
public class VipReceiveReviewResDto {
    private String userName;
    private double reviewRating;
    private String reviewContent;

    @QueryProjection
    public VipReceiveReviewResDto(String userName, double reviewRating, String reviewContent) {
        this.userName = userName;
        this.reviewRating = reviewRating;
        this.reviewContent = reviewContent;
    }
}
