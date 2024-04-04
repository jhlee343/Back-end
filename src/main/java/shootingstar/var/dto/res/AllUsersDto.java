package shootingstar.var.dto.res;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import shootingstar.var.enums.type.UserType;

@Data
public class AllUsersDto {
    private String userUUID;
    private String name;
    private String nickname;
    private String email;
    private String phone;
    private UserType userType;
    private Integer warningCount;

    @QueryProjection
    public AllUsersDto(String userUUID, String name, String nickname, String email, String phone, UserType userType, Integer warningCount) {
        this.userUUID = userUUID;
        this.name = name;
        this.nickname = nickname;
        this.email = email;
        this.phone = phone;
        this.userType = userType;
        this.warningCount = warningCount;
    }
}