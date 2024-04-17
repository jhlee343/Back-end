package shootingstar.var.dto.req;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import shootingstar.var.entity.User;


@Data
public class UserApplyVipDto {
    @NotNull
    private String userUUID;
    @NotNull
    private String vipName;
    @NotNull
    private String vipJob;
    @NotNull
    private String vipCareer;
    @NotNull
    private String vipIntroduce;
    @NotNull
    private String vipEvidenceUrl;
}
