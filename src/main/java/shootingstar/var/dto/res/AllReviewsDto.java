package shootingstar.var.dto.res;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;


@Data
public class AllReviewsDto {
    private String reviewUUID;
    private String nickname;
    private String reviewContent;
    private boolean isShowed;

    @QueryProjection
    public AllReviewsDto(String reviewUUID, String nickname, String reviewContent, boolean isShowed) {
        this.reviewUUID = reviewUUID;
        this.nickname = nickname;
        this.reviewContent = reviewContent;
        this.isShowed = isShowed;
    }
}
