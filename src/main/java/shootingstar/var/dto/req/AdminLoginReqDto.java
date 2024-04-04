package shootingstar.var.dto.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AdminLoginReqDto {
    @NotBlank
    private String loginId;
    @NotBlank
    private String password;
}
