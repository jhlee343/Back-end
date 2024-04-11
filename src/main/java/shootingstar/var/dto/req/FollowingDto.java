package shootingstar.var.dto.req;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

@Data
public class FollowingDto {
    private String nickname;
    private String profileImgUrl;
    private String userUUID;
    @QueryProjection
    public FollowingDto(String nickname, String profileImgUrl, String userUUID){
        this.nickname = nickname;
        this.profileImgUrl = profileImgUrl;
        this.userUUID = userUUID;
    }
}
