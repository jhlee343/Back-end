package shootingstar.var.Service;

import jakarta.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import shootingstar.var.dto.res.AuctionDetailResDto;
import shootingstar.var.dto.res.GetBannerResDto;
import shootingstar.var.dto.res.ProgressAuctionResDto;
import shootingstar.var.dto.res.VipDetailResDto;
import shootingstar.var.entity.*;
import shootingstar.var.entity.ticket.Ticket;
import shootingstar.var.enums.type.AuctionSortType;
import shootingstar.var.enums.type.AuctionType;
import shootingstar.var.enums.type.UserType;
import shootingstar.var.repository.AuctionRepository;
import shootingstar.var.repository.follow.FollowRepository;
import shootingstar.var.repository.review.ReviewRepository;
import shootingstar.var.repository.user.UserRepository;
import shootingstar.var.repository.vip.VipInfoRepository;
import shootingstar.var.repository.banner.BannerRepository;
import shootingstar.var.repository.ticket.TicketRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private AllUserService allUserService;

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
        User basic = new User("33", "실명", "일반인", "000-0000-0000", "test@ttt.com", "helloUrl", UserType.ROLE_BASIC);

        userRepository.save(vip);
        userRepository.save(basic);

        userRepository.flush();

        VipInfo vipInfo = new VipInfo(vip, vip.getName(), "개발자", "경력", "소개", VipApprovalType.APPROVE, "url");
        vipInfoRepository.save(vipInfo);

        vipInfoRepository.flush();

        Auction progressAuction = new Auction(vip, 100000L, LocalDateTime.now(), "위치", "정보", "약속", "img", "img");
        Auction progressAuction1 = new Auction(vip, 100000L, LocalDateTime.now(), "위치", "정보", "약속", "img", "img");
        Auction progressAuction2 = new Auction(vip, 100000L, LocalDateTime.now(), "위치", "정보", "약속", "img", "img");
        Auction progressAuction3 = new Auction(vip, 100000L, LocalDateTime.now(), "위치", "정보", "약속", "img", "img");
        Auction progressAuction4 = new Auction(vip, 100000L, LocalDateTime.now(), "위치", "정보", "약속", "img", "img");

        auctionRepository.save(progressAuction);
        auctionRepository.save(progressAuction1);
        auctionRepository.save(progressAuction2);
        auctionRepository.save(progressAuction3);
        auctionRepository.save(progressAuction4);

        Auction successAuction = new Auction(vip, 100000L, LocalDateTime.now(), "위치", "정보", "약속", "img", "img");
        Auction successAuction1 = new Auction(vip, 100000L, LocalDateTime.now(), "위치", "정보", "약속", "img", "img");
        Auction successAuction2 = new Auction(vip, 100000L, LocalDateTime.now(), "위치", "정보", "약속", "img", "img");
        Auction successAuction3 = new Auction(vip, 100000L, LocalDateTime.now(), "위치", "정보", "약속", "img", "img");

        successAuction.changeAuctionType(AuctionType.SUCCESS);
        successAuction1.changeAuctionType(AuctionType.SUCCESS);
        successAuction2.changeAuctionType(AuctionType.SUCCESS);
        successAuction3.changeAuctionType(AuctionType.SUCCESS);

        auctionRepository.save(successAuction);
        auctionRepository.save(successAuction1);
        auctionRepository.save(successAuction2);
        auctionRepository.save(successAuction3);

        auctionRepository.flush();

        Ticket ticket = new Ticket(successAuction, basic, vip);
        Ticket ticket1 = new Ticket(successAuction1, basic, vip);
        Ticket ticket2 = new Ticket(successAuction2, basic, vip);
        Ticket ticket3 = new Ticket(successAuction3, basic, vip);

        ticketRepository.save(ticket);
        ticketRepository.save(ticket1);
        ticketRepository.save(ticket2);
        ticketRepository.save(ticket3);

        ticketRepository.flush();

        Review review = new Review(basic, vip, ticket, "리뷰 내용", 4);
        Review review1 = new Review(basic, vip, ticket1, "리뷰 내용", 4);
        Review review2 = new Review(basic, vip, ticket2, "리뷰 내용", 4);
        Review review3 = new Review(basic, vip, ticket3, "리뷰 내용", 4);

        reviewRepository.save(review);
        reviewRepository.save(review1);
        reviewRepository.save(review2);
        reviewRepository.save(review3);

        reviewRepository.flush();

        Follow follow = new Follow(basic, vip);
        followRepository.save(follow);
        followRepository.flush();

        //when
        VipDetailResDto vipDetailByVipUUID = userRepository.findVipDetailByVipUUID(vip.getUserUUID(), basic.getUserUUID());

        //then
        System.out.println(vipDetailByVipUUID.toString());
        Assertions.assertThat(vipDetailByVipUUID.getProgressAuctionList().size()).isEqualTo(3);
        Assertions.assertThat(vipDetailByVipUUID.getReceiveReviewList().size()).isEqualTo(3);
    }

    @Test
    @DisplayName("진행중인 일반 경매 조회")
    @Transactional
    public void progressGeneralAuctionList() throws Exception {
        //given
        User vip1 = new User("22", "실명", "유명인1", "000-0000-0000", "test@ttt.com", "helloUrl", UserType.ROLE_VIP);
        User vip2 = new User("22", "실명", "유명인2", "000-0000-0000", "test@ttt.com", "helloUrl", UserType.ROLE_VIP);
        User vip3 = new User("22", "실명", "유명인3", "000-0000-0000", "test@ttt.com", "helloUrl", UserType.ROLE_VIP);

        userRepository.save(vip1);
        userRepository.save(vip2);
        userRepository.save(vip3);

        userRepository.flush();

        Auction progressAuction1 = new Auction(vip1, 100000L, LocalDateTime.now(), "위치", "정보", "약속", "img", "img");
        Auction progressAuction2 = new Auction(vip1, 100000L, LocalDateTime.now(), "위치", "정보", "약속", "img", "img");
        Auction progressAuction3 = new Auction(vip1, 100000L, LocalDateTime.now(), "위치", "정보", "약속", "img", "img");
        Auction progressAuction4 = new Auction(vip2, 100000L, LocalDateTime.now(), "위치", "정보", "약속", "img", "img");
        Auction progressAuction5 = new Auction(vip3, 100000L, LocalDateTime.now(), "위치", "정보", "약속", "img", "img");

        auctionRepository.save(progressAuction1);
        auctionRepository.save(progressAuction2);
        auctionRepository.save(progressAuction3);
        auctionRepository.save(progressAuction4);
        auctionRepository.save(progressAuction5);

        auctionRepository.flush();

        //when

        Pageable pageable = PageRequest.of(0, 10);
        AuctionSortType auctionSortType = AuctionSortType.CREATE_DESC;
        String search = "유명인1";

        Page<ProgressAuctionResDto> progressGeneralAuction = auctionRepository.findProgressGeneralAuction(pageable, null, null);

        //then
        System.out.println(progressGeneralAuction.getContent());
        Assertions.assertThat(progressGeneralAuction.get().count()).isEqualTo(5);
    }

    @Test
    @DisplayName("경매 상세정보 조회")
    @Transactional
    public void auctionDetail() throws Exception {
        //given
        User vip1 = new User("22", "실명", "유명인1", "000-0000-0000", "test@ttt.com", "helloUrl", UserType.ROLE_VIP);
        User vip2 = new User("22", "실명", "유명인2", "000-0000-0000", "test@ttt.com", "helloUrl", UserType.ROLE_VIP);

        userRepository.save(vip1);
        userRepository.save(vip2);

        userRepository.flush();

        Auction progressAuction1 = new Auction(vip1, 100000L, LocalDateTime.now(), "경기도 성남시 분당구 수내동 19-4", "정보", "약속", "img", "img");
        Auction progressAuction2 = new Auction(vip2, 100000L, LocalDateTime.now(), "서울특별시 강남구 선릉로158길 11", "정보", "약속", "img", "img");

        auctionRepository.save(progressAuction1);
        auctionRepository.save(progressAuction2);

        auctionRepository.flush();

        //when
        AuctionDetailResDto auctionDetail1 = allUserService.getAuctionDetail(progressAuction1.getAuctionUUID());
        AuctionDetailResDto auctionDetail2 = allUserService.getAuctionDetail(progressAuction2.getAuctionUUID());

        //then
        System.out.println(auctionDetail1);
        Assertions.assertThat(auctionDetail1.getAuctionUUID()).isEqualTo(progressAuction1.getAuctionUUID());

        System.out.println(auctionDetail2);
        Assertions.assertThat(auctionDetail2.getAuctionUUID()).isEqualTo(progressAuction2.getAuctionUUID());
    }

}