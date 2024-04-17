package shootingstar.var.dto.res;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

@Data
public class AllVipInfosDto {
    private String vipInfoUUID;
    private String vipName;
    private String vipJob;
    private String vipCareer;
    private String vipIntroduce;
    private String vipEvidenceUrl;

    @QueryProjection
    public AllVipInfosDto(String vipInfoUUID, String vipName, String vipJob, String vipCareer, String vipIntroduce, String vipEvidenceUrl) {
        this.vipInfoUUID = vipInfoUUID;
        this.vipName = vipName;
        this.vipJob = vipJob;
        this.vipCareer = vipCareer;
        this.vipIntroduce = vipIntroduce;
        this.vipEvidenceUrl = vipEvidenceUrl;
    }
}