package shootingstar.var.dto.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AdminLoginReqDto {
    @NotBlank
    private String adminLoginId;
    @NotBlank
    private String adminPassword;
}
