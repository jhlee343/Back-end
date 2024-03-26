package shootingstar.var.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VipInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long vipInfoId;

    private String vipInfoUUID;

    @OneToOne
    private User user;

    @NotNull
    private String vipName;

    @NotNull
    private String vipJob;

    @NotNull
    private String vipCareer;

    @NotNull
    private String vipIntroduce;

    @NotNull
    @Enumerated
    private VipApprovalType vipApproval;

    @NotNull
    private String vipEvidenceUrl;

    @Builder
    public VipInfo(String vipInfoUUID, User user, String vipName ,String vipJob,
                   String vipCareer, String vipIntroduce, VipApprovalType vipApprovalType, String vipEvidenceUrl){
        this.vipInfoUUID = UUID.randomUUID().toString();
        this.user = user;
        this.vipName = vipName;
        this.vipCareer = vipCareer;
        this.vipIntroduce = vipIntroduce;
        this.vipJob = vipJob;
        this.vipApproval = vipApprovalType;
        this.vipEvidenceUrl = vipEvidenceUrl;
    }
}
