package shootingstar.var.dto.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AdminSignupReqDto {
    @NotBlank
    private String loginId;
    @NotBlank
    private String password;
    @NotBlank
    private String nickname;
    @NotBlank
    private String secretKey;
}
