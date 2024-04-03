package shootingstar.var.dto.req;

import lombok.Data;

@Data
public class AdminSignupReqDto {
    private String loginId;
    private String password;
    private String secretKey;
}
