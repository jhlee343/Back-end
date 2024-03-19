package shootingstar.var.Service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import shootingstar.var.dto.req.UserSignupReqDto;
import shootingstar.var.entity.User;
import shootingstar.var.repository.UserRepository;

@SpringBootTest
class AuctionServiceTest {

    UserRepository userRepository;
    UserService userService;

    @Test
    void 경매_생성() {
        // given
        // 유저 생성
        UserSignupReqDto userReq = new UserSignupReqDto();
        userReq.setName("홍길동");
        userReq.setNickname("hong1");
        userReq.setEmail("aa@aa.com");
        userReq.setPhoneNumber("010-1111-1111");
        userReq.setProfileImgUrl("");



        // when

        // then
    }

}