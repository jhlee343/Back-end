package shootingstar.var.Service;


import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import shootingstar.var.dto.req.VipInfoEditResDto;
import shootingstar.var.dto.res.VipInfoDto;
import shootingstar.var.entity.User;
import shootingstar.var.entity.VipApprovalType;
import shootingstar.var.entity.VipInfo;
import shootingstar.var.enums.type.UserType;
import shootingstar.var.repository.user.UserRepository;
import shootingstar.var.repository.vip.VipInfoRepository;

@SpringBootTest
class UserServiceTest {
    @Autowired
    UserRepository userRepository;
    @Autowired
    VipInfoRepository vipInfoRepository;
    @Autowired
    VipUserService vipUserService;
    @Test
    @DisplayName("vipinfo edit")
    @Transactional
    public void vipInfoEdit() throws Exception {
        //given
        User vip = new User("22",
                "실명",
                "유명인",
                "000-0000-0000",
                "test@ttt.com",
                "helloUrl",
                UserType.ROLE_VIP);

        userRepository.save(vip);

        userRepository.flush();

        VipInfo vipInfo = new VipInfo(vip, vip.getName(), "개발자", "경력", "소개", VipApprovalType.APPROVE, "url");
        vipInfoRepository.save(vipInfo);

        vipInfoRepository.flush();

        VipInfoDto vipInfoDto = vipUserService.getVipInfo(vip.getUserUUID());

        //변경전 vipinfo확인
        System.out.println(vipInfoDto.getVipCareer()+" "+vipInfoDto.getVipJob()+" "+vipInfoDto.getVipIntroduce());
        VipInfoEditResDto vipInfoEditResDto = new VipInfoEditResDto(
                "무직","경력없음","",""
        );
        vipUserService.editVipInfo(vip.getUserUUID(),vipInfoEditResDto);
        //변경 후 vipinfo 확인
        System.out.println(vipInfo.getVipJob()+" "+ vipInfo.getVipCareer()+" "+vipInfo.getVipIntroduce());

    }
}