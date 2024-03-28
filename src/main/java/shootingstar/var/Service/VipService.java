package shootingstar.var.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import shootingstar.var.dto.res.VipInfoDto;
import shootingstar.var.entity.User;
import shootingstar.var.entity.VipInfo;
import shootingstar.var.exception.CustomException;
import shootingstar.var.repository.Vip.VipInfoRepository;

import java.util.Optional;

import static shootingstar.var.exception.ErrorCode.USER_NOT_FOUND;


@Service
@RequiredArgsConstructor
public class VipService {
    private final VipInfoRepository vipInfoRepository;

    public VipInfoDto getVipInfo(String userUUID){
        VipInfo vipInfo = findByvipInfoUUID(userUUID);
        VipInfoDto vipInfoDto = new VipInfoDto(
                vipInfo.getVipJob(),
                vipInfo.getVipIntroduce(),
                vipInfo.getVipCareer(),
                vipInfo.getVipEvidenceUrl()
        );
        return vipInfoDto;
    }

    public void editInfo(String userUUID, VipInfoDto vipInfoDto){

    }
    public VipInfo findByvipInfoUUID(String userUUID) {
        Optional<VipInfo> optionalVipInfo = vipInfoRepository.findByvipInfoUUID(userUUID);
        if (optionalVipInfo.isEmpty()) {
            throw new RuntimeException();
        }
        return optionalVipInfo.get();
    }

}
