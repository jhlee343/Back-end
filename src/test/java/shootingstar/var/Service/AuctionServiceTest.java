package shootingstar.var.Service;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import shootingstar.var.dto.req.AuctionCreateReqDto;
import shootingstar.var.dto.req.UserSignupReqDto;
import shootingstar.var.entity.User;
import shootingstar.var.exception.CustomException;
import shootingstar.var.exception.ErrorCode;
import shootingstar.var.repository.user.UserRepository;

@SpringBootTest
class AuctionServiceTest {

    UserRepository userRepository;
    UserService userService;
    @Autowired
    AuctionService auctionService;

    @Test
    void 경매_생성() {
        // given
        // 유저 생성
        UserSignupReqDto userReq = new UserSignupReqDto();
        userReq.setUserName("홍길동");
        userReq.setNickname("hong1");
        userReq.setEmail("aa@aa.com");
        userReq.setPhoneNumber("010-1111-1111");
        userReq.setProfileImgUrl("");



        // when

        // then
    }

    @Test
    @Transactional
    void 비관적락_테스트() throws InterruptedException {
        // given
        int numberOfThreads = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        // when
        for (int i = 0; i < numberOfThreads; i++) {
            final int cnt = i;
            executorService.submit(() -> {
                try {
                    AuctionCreateReqDto reqDto = new AuctionCreateReqDto();
                    reqDto.setMinBidAmount(100000L);
                    reqDto.setMeetingDate("2024-05-01T18:00:00");
                    reqDto.setMeetingLocation("대전 서구 월평동");
                    reqDto.setMeetingInfoText("저 좋은 사람입니다." + cnt);
                    reqDto.setMeetingPromiseText("진짭니다. 약속드립니다.");

                    String userUUID = "fb3ebae0-e0bb-470f-86e1-20a69384a4d6";

                    auctionService.create(reqDto, userUUID);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        executorService.shutdown();
    }

}