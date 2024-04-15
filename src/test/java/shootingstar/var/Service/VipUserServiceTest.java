package shootingstar.var.Service;


import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import shootingstar.var.dto.req.VipInfoEditResDto;
import shootingstar.var.dto.res.UserAuctionSuccessResDto;
import shootingstar.var.dto.res.VipInfoDto;
import shootingstar.var.entity.auction.Auction;
import shootingstar.var.entity.User;
import shootingstar.var.entity.VipApprovalType;
import shootingstar.var.entity.VipInfo;
import shootingstar.var.enums.type.AuctionType;
import shootingstar.var.enums.type.UserType;
import shootingstar.var.repository.AuctionRepository;
import shootingstar.var.repository.user.UserRepository;
import shootingstar.var.repository.vip.VipInfoRepository;

import java.time.LocalDateTime;

@SpringBootTest
class VipUserServiceTest {
    @Autowired
    UserRepository userRepository;
    @Autowired
    VipInfoRepository vipInfoRepository;
    @Autowired
    VipUserService vipUserService;
    @Autowired
    AuctionRepository auctionRepository;
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

//    @Test
//    @DisplayName("vip Auction Success load")
//    public void vipAuctionSuccess() throws Exception{
//        //vip auction 현재날짜 전 날짜 후로 구분해서 가져오기
//
//        //user 생성
//        User vip1 = new User("22", "실명", "유명인1", "000-0000-0000", "test@ttt.com", "helloUrl", UserType.ROLE_VIP);
//
//        userRepository.save(vip1);
//
//        userRepository.flush();
//
//        Auction progressAuction1 = new Auction(vip1, 100000L, LocalDateTime.of(2025,01,01, 01,01), "위치", "정보", "약속", "img", "img");
//        Auction progressAuction2 = new Auction(vip1, 100000L, LocalDateTime.of(2025,01,01, 01,01), "위치", "정보", "약속", "img", "img");
//        Auction progressAuction3 = new Auction(vip1, 100000L, LocalDateTime.of(2023,01,01, 01,01), "위치", "정보", "약속", "img", "img");
//        Auction progressAuction4 = new Auction(vip1, 100000L, LocalDateTime.of(2023,01,01, 01,01), "위치", "정보", "약속", "img", "img");
//        Auction progressAuction5 = new Auction(vip1, 100000L, LocalDateTime.of(2023,01,01, 01,01), "위치", "정보", "약속", "img", "img");
//
//        progressAuction1.changeAuctionType(AuctionType.SUCCESS);
//        progressAuction2.changeAuctionType(AuctionType.SUCCESS);
//        progressAuction3.changeAuctionType(AuctionType.SUCCESS);
//        progressAuction4.changeAuctionType(AuctionType.SUCCESS);
//        progressAuction5.changeAuctionType(AuctionType.SUCCESS);
//
//        auctionRepository.save(progressAuction1);
//        auctionRepository.save(progressAuction2);
//        auctionRepository.save(progressAuction3);
//        auctionRepository.save(progressAuction4);
//        auctionRepository.save(progressAuction5);
//
//        auctionRepository.flush();
//
//        Page<UserAuctionSuccessResDto> userAuctionSuccessResBeforeDtos = vipUserService.getVipUserAuctionSuccessBefore(vip1.getUserUUID(),Pageable.unpaged());
//        System.out.println(userAuctionSuccessResBeforeDtos.getSize());
//        System.out.println(userAuctionSuccessResBeforeDtos.getContent());
//
//        Page<UserAuctionSuccessResDto> userAuctionSuccessResAfterDtos = vipUserService.getVipUserAuctionSuccessAfter(vip1.getUserUUID(),Pageable.unpaged());
//        System.out.println(userAuctionSuccessResAfterDtos.getSize());
//        System.out.println(userAuctionSuccessResAfterDtos.getContent());
//
//    }
}