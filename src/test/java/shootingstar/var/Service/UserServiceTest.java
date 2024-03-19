package shootingstar.var.Service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import shootingstar.var.Service.dto.UserProfileDto;
import shootingstar.var.entity.Follow;
import shootingstar.var.entity.User;
import shootingstar.var.entity.UserType;
import shootingstar.var.repository.UserRepository;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Test
    @Transactional
    public void follow() throws Exception{
        //
    }
    @Test
    @Transactional
    public void saveUser() throws Exception {
        //given
        User user1 = new User(
                "3390072659",
                "이재현",
                "dlwogus",
                "+82 10-0000-000",
                "w203802@gmail.com",
                "http://k.kakaocdn.net/dn/bWOklw/btsEJdyuAoJ/DgLQ4aHPSPshqJyGPEkzs0/img_640x640.jpg",
                UserType.ROLE_VIP
        );

        User user2 = new User(
                "3390072653",
                "이재삼",
                "wotka",
                "+82 10-0000-000",
                "aaaaaa@naver.com",
                "http://k.kakaocdn.net/dn/bWOklw/btsEJdyuAoJ/DgLQ4daHPSPsyqJyGPEkzs0/img_640x640.jpg",
                UserType.ROLE_BASIC
        );

        User user3 = new User(
                "3390072655",
                "이재사",
                "wotk",
                "+82 10-0000-000",
                "wwwwww@naver.com",
                "http://k.kakaocdn.net/dn/bWOklw/btsEJdyuAoJ/DgLQ4aHdsPSPsyqJyGPEkzs0/img_640x640.jpg",
                UserType.ROLE_BASIC
        );

        //when
        User saveUser1 = userRepository.save(user1);
        User saveUser2 = userRepository.save(user2);
        User saveUser3 = userRepository.save(user3);
        userRepository.flush();

        UUID userId1 = user1.getUserUUID();
        UUID userId2 = user2.getUserUUID();
        UUID userId3 = user3.getUserUUID();
        System.out.println(userId1);
        System.out.println(userId2);
        System.out.println(userId3);

        Long findUserId = userRepository.findByUserUUID(userId1).get().getUserId();
        //then
        assertThat(saveUser1.getUserId()).isEqualTo(userId1);
        assertThat(userId1).isEqualTo(findUserId);
    }
}