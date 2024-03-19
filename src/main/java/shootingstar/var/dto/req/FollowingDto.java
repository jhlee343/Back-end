package shootingstar.var.dto.req;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

@Data
public class FollowingDto {
    private String nickname;
    private String profileImgUrl;
    private Long id;

    @QueryProjection
    public FollowingDto(String nickname, String profileImgUrl, Long id){
        this.nickname = nickname;
        this.profileImgUrl = profileImgUrl;
        this.id = id;
    }
}
