package shootingstar.var.dto.req;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SendAuthCodeReqDto {
    @Email
    @NotBlank
    private String email;
}
