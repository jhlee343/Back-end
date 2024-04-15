package shootingstar.var.dto.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BannerReqDto {
    @NotBlank
    private String bannerImgUrl;
    private String targetUrl;
}
