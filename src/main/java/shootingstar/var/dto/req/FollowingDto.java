package shootingstar.var.dto.req;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

@Data
public class FollowingDto {
    private String nickname;
    private String profileImgUrl;
    private String userUUID;
    private String followUUID;
    @QueryProjection
    public FollowingDto(String nickname, String profileImgUrl, String userUUID, String followUUID){
        this.nickname = nickname;
        this.profileImgUrl = profileImgUrl;
        this.userUUID = userUUID;
        this.followUUID = followUUID;
    }
}
