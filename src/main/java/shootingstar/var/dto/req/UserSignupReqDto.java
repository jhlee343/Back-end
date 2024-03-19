package shootingstar.var.dto.req;

import lombok.Data;

@Data
public class UserSignupReqDto {
    private String id;
    private String name;
    private String nickname;
    private String email;
    private String phoneNumber;
    private String profileImgUrl;
}
