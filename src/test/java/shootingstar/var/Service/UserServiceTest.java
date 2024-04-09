package shootingstar.var.Service;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import shootingstar.var.dto.req.UserProfileDto;
import shootingstar.var.entity.User;
import shootingstar.var.enums.type.UserType;
import shootingstar.var.repository.user.UserRepository;

@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("프로필 불러오기")
    @Transactional
    public void getProfile() throws Exception {
        User vip = new User("22", "실명", "유명인", "000-0000-0000", "test@ttt.com", "helloUrl", UserType.ROLE_VIP);
        User basic = new User("33", "실명", "일반인", "000-0000-0000", "test@ttt.com", "helloUrl", UserType.ROLE_BASIC);

        userRepository.save(vip);
        userRepository.save(basic);

        userRepository.flush();

        UserProfileDto userProfileDto = userService.getProfile(vip.getUserUUID());
        System.out.println(userProfileDto.getNickname()+" "+ userProfileDto.getPoint()+" "+userProfileDto.getSubscribeExpiration());
        UserProfileDto userProfileDto1 = userService.getProfile(basic.getUserUUID());
        System.out.println(userProfileDto1.getNickname()+" "+ userProfileDto1.getPoint()+" "+userProfileDto1.getSubscribeExpiration());
    }
}
