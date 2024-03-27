package shootingstar.var.dto.res;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import shootingstar.var.entity.User;
import shootingstar.var.entity.VipApprovalType;

import java.util.UUID;

@Data
public class VipInfoDto {
    @NotNull
    private String vipJob;
    @NotNull
    private String vipCareer;
    @NotNull
    private String vipIntroduce;
    @NotNull
    private String vipEvidenceUrl;

    @Builder
    public VipInfoDto(String vipJob,
                   String vipCareer, String vipIntroduce, String vipEvidenceUrl){
        this.vipCareer = vipCareer;
        this.vipIntroduce = vipIntroduce;
        this.vipJob = vipJob;
        this.vipEvidenceUrl = vipEvidenceUrl;
    }
}
