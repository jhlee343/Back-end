package shootingstar.var.dto.res;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

import java.time.LocalDateTime;


@Data
public class AllReviewsDto {
    private String reviewUUID;
    private String writerNickname;
    private String receiverNickname;
    private String reviewContent;
    private LocalDateTime createdTime;
    private boolean isShowed;

    @QueryProjection
    public AllReviewsDto(String reviewUUID, String writerNickname, String receiverNickname, String reviewContent, LocalDateTime createdTime, boolean isShowed) {
        this.reviewUUID = reviewUUID;
        this.writerNickname = writerNickname;
        this.receiverNickname = receiverNickname;
        this.reviewContent = reviewContent;
        this.createdTime = createdTime;
        this.isShowed = isShowed;
    }
}
