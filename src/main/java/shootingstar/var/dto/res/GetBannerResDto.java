package shootingstar.var.dto.res;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class GetBannerResDto {
    private String bannerImgUrl;
    private String targetUrl;

    @QueryProjection
    public GetBannerResDto(String bannerImgUrl, String targetUrl) {
        this.bannerImgUrl = bannerImgUrl;
        this.targetUrl = targetUrl;
    }
}
