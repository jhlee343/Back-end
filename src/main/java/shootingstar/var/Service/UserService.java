package shootingstar.var.Service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shootingstar.var.Service.dto.FollowingDto;
import shootingstar.var.Service.dto.UserProfileDto;
import shootingstar.var.Service.dto.UserSignupReqDto;
import shootingstar.var.entity.Follow;
import shootingstar.var.entity.User;
import shootingstar.var.entity.UserType;
import shootingstar.var.jwt.JwtTokenProvider;
import shootingstar.var.repository.FollowRepository;
import shootingstar.var.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final FollowRepository followRepository;

    public void signup(UserSignupReqDto reqDto) {
        User user = User.builder()
                .kakaoId(reqDto.getId())
                .name(reqDto.getName())
                .nickname(reqDto.getNickname())
                .phone(reqDto.getPhoneNumber())
                .email(reqDto.getEmail())
                .profileImgUrl(reqDto.getProfileImgUrl())
                .userType(UserType.ROLE_BASIC)
                .build();

        userRepository.save(user);
    }

    public boolean checkNicknameDuplicate(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    public boolean checkVIP(HttpServletRequest request) {
        UUID userUUID = jwtTokenProvider.getUserUUIDByRequest(request);
        User user = findByuserUUID(String.valueOf(userUUID));
        if (user.getUserType().equals(UserType.ROLE_VIP)) {
            //vip인 경우 true
            return true;
        } else {
            //vip가 아닌경우 false
            return false;
        }
    }

    public UserProfileDto getProfile(String nickname) {
        User user = findByNickname(nickname);
        UserProfileDto userProfileDto = new UserProfileDto(user.getNickname(), user.getProfileImgUrl(), user.getDonationPrice(), user.getPoint(), user.getSubscribe(), user.getUserType());
        return userProfileDto;
    }

    public List<FollowingDto> findAllFollowing(HttpServletRequest request) {
        UUID userUUID = jwtTokenProvider.getUserUUIDByRequest(request);
        return followRepository.findAllByFollowerId(String.valueOf(userUUID));
    }

    @Transactional
    public void follow(String followingId, HttpServletRequest request) {
        UUID followerId = jwtTokenProvider.getUserUUIDByRequest(request);
        User follower = findByuserUUID(String.valueOf(followerId));
        User following = findByuserUUID(followingId);
        UUID followUUID = UUID.randomUUID();
        Follow follow = new Follow(followUUID,follower,following);
        followRepository.save(follow);
    }

    @Transactional
    public void unFollow(UUID followUUID) {
        Follow follow = findFollowingByFollowUUID(followUUID);
        followRepository.delete(follow);
    }

    private Follow findFollowingByFollowUUID(UUID followUUID) {
        Optional<Follow> followOptional = followRepository.findByFollowUUID(followUUID);
        if (followOptional.isEmpty()) {
            throw new RuntimeException();
        }
        return followOptional.get();
    }

    public User findByNickname(String nickname) {
        Optional<User> optionalUser = userRepository.findByNickname(nickname);
        if (optionalUser.isEmpty()) {
            throw new RuntimeException();
        }
        return optionalUser.get();
    }

    public User findByuserUUID(String userUUID) {
        Optional<User> optionalUser = userRepository.findByUserUUID(UUID.fromString(userUUID));
        if (optionalUser.isEmpty()) {
            throw new RuntimeException();
        }
        return optionalUser.get();
    }

}