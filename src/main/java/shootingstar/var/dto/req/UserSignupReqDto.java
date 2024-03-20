package shootingstar.var.dto.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserSignupReqDto {
    @NotBlank
    private String id;
    @NotBlank
    private String name;
    @NotBlank
    private String nickname;
    @NotBlank
    private String email;
    @NotBlank
    private String phoneNumber;
    @NotBlank
    private String profileImgUrl;
}
