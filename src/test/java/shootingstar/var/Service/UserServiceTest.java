package shootingstar.var.Service;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import shootingstar.var.dto.req.FollowingDto;
import shootingstar.var.dto.req.UserProfileDto;
import shootingstar.var.entity.Follow;
import shootingstar.var.entity.User;
import shootingstar.var.entity.VipApprovalType;
import shootingstar.var.entity.VipInfo;
import shootingstar.var.enums.type.UserType;
import shootingstar.var.repository.follow.FollowRepository;
import shootingstar.var.repository.user.UserRepository;
import shootingstar.var.repository.vip.VipInfoRepository;

import java.util.List;

@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private VipInfoRepository vipInfoRepository;
    @Autowired
    private BasicUserService basicUserService;
    @Autowired
    private AdminService adminService;
    @Autowired
    private FollowRepository followRepository;

    @Test
    @DisplayName("팔로우리스트 가져오기")
    @Transactional
    public void getFollowList()  throws  Exception{
        User vip1 = new User("11", "실명1", "유명인1", "000-0000-0000", "test@ttt.com", "helloUrl", UserType.ROLE_VIP);
        User vip2 = new User("22", "실명2", "유명인2", "000-0000-0000", "test@ttt.com", "helloUrl", UserType.ROLE_VIP);
        User vip3 = new User("33", "실명3", "유명인3", "000-0000-0000", "test@ttt.com", "helloUrl", UserType.ROLE_VIP);
        User basic = new User("44", "실명", "일반인", "000-0000-0000", "test@ttt.com", "helloUrl", UserType.ROLE_BASIC);

        userRepository.save(vip1);
        userRepository.save(vip2);
        userRepository.save(vip3);
        userRepository.save(basic);

        userRepository.flush();

        userService.follow(vip1.getUserUUID(), basic.getUserUUID());
        userService.follow(vip2.getUserUUID(), basic.getUserUUID());
        userService.follow(vip3.getUserUUID(), basic.getUserUUID());
        List<FollowingDto> followingDto =userService.findAllFollowing(basic.getUserUUID());
        System.out.println(followingDto);



    }

    @Test
    @DisplayName("unfollow")
    @Transactional
    public void unfollow() throws Exception{
        User vip1 = new User("11", "실명1", "유명인1", "000-0000-0000", "test@ttt.com", "helloUrl", UserType.ROLE_VIP);
        User vip2 = new User("22", "실명2", "유명인2", "000-0000-0000", "test@ttt.com", "helloUrl", UserType.ROLE_VIP);
        User vip3 = new User("33", "실명3", "유명인3", "000-0000-0000", "test@ttt.com", "helloUrl", UserType.ROLE_VIP);
        User basic = new User("44", "실명", "일반인", "000-0000-0000", "test@ttt.com", "helloUrl", UserType.ROLE_BASIC);

        userRepository.save(vip1);
        userRepository.save(vip2);
        userRepository.save(vip3);
        userRepository.save(basic);

        userRepository.flush();

        //userService.unFollow();
        Follow follow1 = new Follow(basic, vip1);
        Follow follow2 = new Follow(basic, vip2);
        Follow follow3 = new Follow(basic, vip3);

        followRepository.save(follow1);
        followRepository.save(follow2);
        followRepository.save(follow3);

        followRepository.flush();
        List<FollowingDto> followingDto =userService.findAllFollowing(basic.getUserUUID());
        System.out.println(followingDto);
        System.out.println(followingDto.size());


        userService.unFollow(follow1.getFollowUUID());
        List<FollowingDto> followingDto1 =userService.findAllFollowing(basic.getUserUUID());
        System.out.println(followingDto1);
        System.out.println(followingDto1.size());
    }
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
        System.out.println(userProfileDto.getNickname()+" "+ userProfileDto.getPoint()+" "+userProfileDto.getRating());
        UserProfileDto userProfileDto1 = userService.getProfile(basic.getUserUUID());
        System.out.println(userProfileDto1.getNickname()+" "+ userProfileDto1.getPoint()+" "+userProfileDto1.getRating());
    }

//    @Test
//    @DisplayName("vip 신청 상태 조회")
//    @Transactional
//    public void applyCheck() throws Exception {
//        User basic = new User("33", "실명", "일반인", "000-0000-0000", "test@ttt.com", "helloUrl", UserType.ROLE_BASIC);
//        userRepository.save(basic);
//        userRepository.flush();
//
//        VipInfo vipInfo = new VipInfo(basic,"일반인","직업","경력","소개", VipApprovalType.STANDBY,"url");
//        vipInfoRepository.save(vipInfo);
//        vipInfoRepository.flush();
//
//        basicUserService.applyCheck(basic.getUserUUID());
//        System.out.println(basicUserService.applyCheck(basic.getUserUUID()));
//
//        adminService.vipInfoChange(vipInfo.getVipInfoUUID(), "APPROVE");
//        System.out.println(basicUserService.applyCheck(basic.getUserUUID()));
//    }
}
