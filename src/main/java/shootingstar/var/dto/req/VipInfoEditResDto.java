package shootingstar.var.dto.req;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
public class VipInfoEditResDto {
    @NotNull
    private String vipJob;
    @NotNull
    private String vipCareer;
    @NotNull
    private String vipIntroduce;
    @NotNull
    private String vipEvidenceUrl;

    @Builder
    public VipInfoEditResDto(String vipJob, String vipCareer, String vipIntroduce,
                             String vipEvidenceUrl){
        this.vipJob = vipJob;
        this.vipCareer = vipCareer;
        this.vipIntroduce = vipIntroduce;
        this.vipEvidenceUrl = vipEvidenceUrl;
    }
}
