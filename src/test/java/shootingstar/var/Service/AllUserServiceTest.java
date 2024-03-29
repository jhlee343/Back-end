package shootingstar.var.Service;

import jakarta.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import shootingstar.var.dto.res.GetBannerResDto;
import shootingstar.var.dto.res.VipDetailResDto;
import shootingstar.var.entity.*;
import shootingstar.var.entity.ticket.Ticket;
import shootingstar.var.enums.type.AuctionType;
import shootingstar.var.repository.AuctionRepository;
import shootingstar.var.repository.Review.ReviewRepository;
import shootingstar.var.repository.UserRepository;
import shootingstar.var.repository.Vip.VipInfoRepository;
import shootingstar.var.repository.banner.BannerRepository;
import shootingstar.var.repository.ticket.TicketRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AllUserServiceTest {

    @Autowired
    private BannerRepository bannerRepository;
    @Autowired
    private AuctionRepository auctionRepository;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private VipInfoRepository vipInfoRepository;
    @Autowired
    private TicketRepository ticketRepository;

    @Test
    @DisplayName("베너 생성 테스트")
    @Transactional
    public void createBanner() throws Exception {
        //given
        String imgUrl = "www.testImgUrl.com";
        String targetUrl = "www.targetUrl.com";

        //when
        Banner banner = new Banner(imgUrl, targetUrl);
        bannerRepository.save(banner);
        bannerRepository.flush();

        Long bannerId = banner.getBannerId();

        Optional<Banner> optionalBanner = bannerRepository.findById(bannerId);
        if (optionalBanner.isEmpty()) throw new RuntimeException("베너를 찾을 수 없음");

        //then
        Banner findBanner = optionalBanner.get();

        Assertions.assertThat(banner.getBannerUUID()).isEqualTo(findBanner.getBannerUUID());
    }

    @Test
    @DisplayName("모든 베너 조회")
    @Transactional
    public void findAllBanner() throws Exception {
        //given
        String imgUrl = "www.testImgUrl.com";
        String targetUrl = "www.targetUrl.com";

        //when
        Banner banner = new Banner(imgUrl, targetUrl);
        Banner banner1 = new Banner(imgUrl, targetUrl);
        Banner banner2 = new Banner(imgUrl, targetUrl);
        Banner banner3 = new Banner(imgUrl, targetUrl);
        Banner banner4 = new Banner(imgUrl, targetUrl);
        Banner banner5 = new Banner(imgUrl, targetUrl);
        Banner banner6 = new Banner(imgUrl, targetUrl);
        Banner banner7 = new Banner(imgUrl, targetUrl);

        bannerRepository.save(banner);
        bannerRepository.save(banner1);
        bannerRepository.save(banner2);
        bannerRepository.save(banner3);
        bannerRepository.save(banner4);
        bannerRepository.save(banner5);
        bannerRepository.save(banner6);
        bannerRepository.save(banner7);

        bannerRepository.flush();

        //then
        List<GetBannerResDto> allBanner = bannerRepository.findAllBanner();
        System.out.println(allBanner);
        Assertions.assertThat(allBanner).isNotEmpty();
    }

    @Test
    @DisplayName("vip 정보 불러오기")
    @Transactional
    public void getVipInfo() throws Exception {
        //given
        User vip = new User("22", "실명", "유명인", "000-0000-0000", "test@ttt.com", "helloUrl", UserType.ROLE_VIP);
        userRepository.save(vip);

        User basic = new User("33", "실명", "일반인", "000-0000-0000", "test@ttt.com", "helloUrl", UserType.ROLE_BASIC);
        userRepository.save(basic);

        userRepository.flush();

        VipInfo vipInfo = new VipInfo("dd", vip, vip.getName(), "개발자", "경력", "소개", VipApprovalType.APPROVE, "url");
        vipInfoRepository.save(vipInfo);
        vipInfoRepository.flush();

        Auction progressAuction = new Auction(vip, 100000L, LocalDateTime.now(), "위치", "정보", "약속", "img", "img");
        auctionRepository.save(progressAuction);
        Auction successAuction = new Auction(vip, 100000L, LocalDateTime.now(), "위치", "정보", "약속", "img", "img");
        auctionRepository.save(successAuction);
        successAuction.changeAuctionType(AuctionType.SUCCESS);
        auctionRepository.flush();

        Ticket ticket = new Ticket(successAuction, basic, vip);
        ticketRepository.save(ticket);
        ticketRepository.flush();

        Review review = new Review(basic, vip, ticket, "리뷰 내용", 4);
        reviewRepository.save(review);
        reviewRepository.flush();

        //when
        VipDetailResDto vipDetailByVipUUID = userRepository.findVipDetailByVipUUID(vip.getUserUUID());

        //then
        System.out.println(vipDetailByVipUUID.toString());

    }
}