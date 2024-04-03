package shootingstar.var.dto.req;

import lombok.Data;

@Data
public class AdminLoginReqDto {
    private String loginId;
    private String password;
}
