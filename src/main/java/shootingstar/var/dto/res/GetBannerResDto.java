package shootingstar.var.dto.res;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GetBannerResDto {
    private String bannerImgUrl;
    private String targetUrl;
}
