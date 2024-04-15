package shootingstar.var.dto.res;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import shootingstar.var.enums.type.UserType;

import java.math.BigDecimal;

@Data
public class AllUsersDto {
    private String kakaoId;
    private String userUUID;
    private String name;
    private String nickname;
    private String email;
    private String phone;
    private BigDecimal point;
    private UserType userType;
    private Integer warningCount;

    @QueryProjection
    public AllUsersDto(String kakaoId, String userUUID, String name, String nickname, String email, String phone, BigDecimal point, UserType userType, Integer warningCount) {
        this.kakaoId = kakaoId;
        this.userUUID = userUUID;
        this.name = name;
        this.nickname = nickname;
        this.email = email;
        this.phone = phone;
        this.point = point;
        this.userType = userType;
        this.warningCount = warningCount;
    }
}