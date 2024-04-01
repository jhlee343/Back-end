package shootingstar.var.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import shootingstar.var.dto.req.UserSignupReqDto;
import shootingstar.var.dto.res.GetBannerResDto;
import shootingstar.var.dto.res.VipDetailResDto;
import shootingstar.var.dto.res.VipProgressAuctionResDto;
import shootingstar.var.entity.User;
import shootingstar.var.enums.type.UserType;
import shootingstar.var.exception.CustomException;
import shootingstar.var.exception.ErrorCode;
import shootingstar.var.repository.user.UserRepository;
import shootingstar.var.repository.banner.BannerRepository;
import shootingstar.var.util.MailRedisUtil;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AllUserService {
    private final UserRepository userRepository;
    private final BannerRepository bannerRepository;

    private final MailRedisUtil mailRedisUtil;
    private final CheckDuplicateService duplicateService;

    public void signup(UserSignupReqDto reqDto) {
        if (duplicateService.checkEmailDuplicate(reqDto.getEmail())) {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }
        if (duplicateService.checkNicknameDuplicate(reqDto.getNickname())) {
            throw new CustomException(ErrorCode.DUPLICATE_NICKNAME);
        }

        if (mailRedisUtil.hasKey(reqDto.getEmail()) && mailRedisUtil.getData(reqDto.getEmail()).equals("validate")) { // 이메일 인증을 받은 이메일 인지 확인

            User user = User.builder()
                    .kakaoId(reqDto.getKakaoId())
                    .name(reqDto.getUserName())
                    .nickname(reqDto.getNickname())
                    .phone(reqDto.getPhoneNumber())
                    .email(reqDto.getEmail())
                    .profileImgUrl(reqDto.getProfileImgUrl())
                    .userType(UserType.ROLE_BASIC)
                    .build();

            userRepository.save(user);
            mailRedisUtil.deleteData(reqDto.getEmail());
        } else {
            throw new CustomException(ErrorCode.VALIDATE_ERROR_EMAIL);
        }
    }

    public List<GetBannerResDto> getBanner() {
        return bannerRepository.findAllBanner();
    }

    public VipDetailResDto getVipDetail(String vipUUID) {
        User vip = checkUserAndVipRole(vipUUID);

        return userRepository.findVipDetailByVipUUID(vipUUID);
    }

    public Page<VipProgressAuctionResDto> getVipProgressAuction(String vipUUID, Pageable pageable) {
        User vip = checkUserAndVipRole(vipUUID);

        return userRepository.findVipProgressAuction(vipUUID, pageable);
    }

    private User checkUserAndVipRole(String vipUUID) {
        User vip = userRepository.findByUserUUID(vipUUID)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (!vip.getUserType().equals(UserType.ROLE_VIP)) {
            throw new CustomException(ErrorCode.VIP_INFO_NOT_FOUND);
        }

        return vip;
    }
}