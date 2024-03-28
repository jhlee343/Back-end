package shootingstar.var.dto.req;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import shootingstar.var.entity.UserType;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class UserProfileDto {
    private String nickname;
    private String profileImgUrl;
    private Long donation_price;
    private BigDecimal point;
    private LocalDateTime subscribe;
    private UserType user_type;
}
