package shootingstar.var.dto.res;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

@Data
public class VipListResDto {
    private String vipUUID;
    private String profileImgUrl;
    private String vipNickname;
    private Double vipRate;
    private boolean isFollow;

    @QueryProjection
    public VipListResDto(String vipUUID, String profileImgUrl, String vipNickname, Double vipRate, boolean isFollow) {
        this.vipUUID = vipUUID;
        this.profileImgUrl = profileImgUrl;
        this.vipNickname = vipNickname;
        this.vipRate = vipRate;
        this.isFollow = isFollow;
    }
}
