package shootingstar.var.Service;

import jakarta.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import shootingstar.var.dto.res.GetBannerResDto;
import shootingstar.var.entity.Banner;
import shootingstar.var.repository.banner.BannerRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AllUserServiceTest {

    @Autowired
    private BannerRepository bannerRepository;

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
}