package shootingstar.var.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shootingstar.var.Service.dto.UserProfileDto;
import shootingstar.var.Service.dto.UserSignupReqDto;
import shootingstar.var.entity.User;
import shootingstar.var.entity.UserType;
import shootingstar.var.repository.UserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

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
    public boolean checkNicknameDuplicate(String nickname){
        return userRepository.existsByNickname(nickname);
    }

    public boolean checkVIP(String nickname){
        User user = findByNickname(nickname);
        if(user.getUserType().equals(UserType.ROLE_VIP)){
            //vip인 경우 true
            return true;
        }
        else {
            //vip가 아닌경우 false
            return false;
        }
    }
    public UserProfileDto getProfile(String nickname){
        User user =findByNickname(nickname);
        UserProfileDto userProfileDto = new UserProfileDto(user.getNickname(), user.getProfileImgUrl(), user.getDonationPrice(), user.getPoint(), user.getSubscribe(), user.getUserType());
        return userProfileDto;
    }


    public User findByNickname(String nickname){
        Optional<User> optionalUser = userRepository.findByNickname(nickname);
        if(optionalUser.isEmpty()){
            throw new RuntimeException();
        }
        return optionalUser.get();
    }



}
