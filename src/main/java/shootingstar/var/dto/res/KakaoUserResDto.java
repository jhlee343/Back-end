package shootingstar.var.dto.res;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class KakaoUserResDto {
    private String id;
    private String name;
    private String email;
    private String phoneNumber;
    private String profileImgUrl;
}
