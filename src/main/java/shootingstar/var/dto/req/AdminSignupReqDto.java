package shootingstar.var.dto.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AdminSignupReqDto {
    @NotBlank
    private String adminLoginId;
    @NotBlank
    private String adminPassword;
    @NotBlank
    private String adminNickname;
    @NotBlank
    private String adminSecretKey;
}
