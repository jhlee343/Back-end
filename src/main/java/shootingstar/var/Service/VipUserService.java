package shootingstar.var.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shootingstar.var.dto.req.VipInfoEditResDto;
import shootingstar.var.dto.res.UserAuctionInvalidityResDto;
import shootingstar.var.dto.res.UserAuctionParticipateResDto;
import shootingstar.var.dto.res.UserAuctionSuccessResDto;
import shootingstar.var.dto.res.VipInfoDto;
import shootingstar.var.entity.Auction;
import shootingstar.var.entity.User;
import shootingstar.var.entity.VipInfo;
import shootingstar.var.exception.CustomException;
import shootingstar.var.repository.AuctionRepository;
import shootingstar.var.repository.user.UserRepository;
import shootingstar.var.repository.vip.VipInfoRepository;

import java.util.Optional;

import static shootingstar.var.exception.ErrorCode.USER_NOT_FOUND;
import static shootingstar.var.exception.ErrorCode.VIP_INFO_NOT_FOUND;


@Service
@RequiredArgsConstructor
public class VipUserService {
    private final VipInfoRepository vipInfoRepository;
    private final UserRepository userRepository;
    private final AuctionRepository auctionRepository;

    public VipInfoDto getVipInfo(String userUUID){
        User user = findByUserUUID(userUUID);
        VipInfo vipInfo = findVipInfoByUser(user);
        return new VipInfoDto(
                vipInfo.getVipJob(),
                vipInfo.getVipIntroduce(),
                vipInfo.getVipCareer()
        );
    }

    @Transactional
    public void editVipInfo(String userUUID, VipInfoEditResDto vipInfoEdit){
        User user =findByUserUUID(userUUID);
        VipInfo vipInfo = findVipInfoByUser(user);
        if(vipInfoEdit.getVipJob()!=null){
            vipInfo.changeVipJob(vipInfoEdit.getVipJob());
        }
        if(vipInfoEdit.getVipCareer()!=null){
            vipInfo.changeVipCareer(vipInfoEdit.getVipCareer());
        }
        if(vipInfoEdit.getVipIntroduce()!=null){
            vipInfo.changeVipIntroduce(vipInfoEdit.getVipIntroduce());
        }
        if(vipInfoEdit.getVipEvidenceUrl()!=null){
            vipInfo.changeVipEvidenceUrl(vipInfoEdit.getVipEvidenceUrl());
        }

        vipInfoRepository.save(vipInfo);
    }
    public User findByUserUUID(String userUUID) {
        Optional<User> optionalUser = userRepository.findByUserUUID(userUUID);
        if (optionalUser.isEmpty()) {
            throw new CustomException(USER_NOT_FOUND);
        }
        return optionalUser.get();
    }
    public VipInfo findVipInfoByUser(User user) {
        Optional<VipInfo> optionalVipInfo = vipInfoRepository.findVipInfoByUser(user);
        if (optionalVipInfo.isEmpty()) {
            throw new CustomException(VIP_INFO_NOT_FOUND);
        }
        return optionalVipInfo.get();
    }

    public Page<UserAuctionSuccessResDto> getVipUserAuctionSuccessBefore(String userUUID, Pageable pageable){
        return auctionRepository.findAllVipSuccessBeforeByUserUUID(userUUID,pageable);
        //return null;
    }
    public Page<UserAuctionSuccessResDto> getVipUserAuctionSuccessAfter(String userUUID, Pageable pageable){
        return null;
    }
    public Page<UserAuctionParticipateResDto> getVipUserAuctionProgress(String userUUID, Pageable pageable){
        return auctionRepository.findAllVipProgressByUserUUID(userUUID,pageable);
    }
    public Page<UserAuctionInvalidityResDto> getVipUserAuctionInvalidity(String userUUID, Pageable pageable){
        return auctionRepository.findAllVipInvalidityByUserUUID(userUUID,pageable);
    }

}
