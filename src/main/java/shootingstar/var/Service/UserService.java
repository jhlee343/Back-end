package shootingstar.var.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import shootingstar.var.Service.dto.UserSignupReqDto;
import shootingstar.var.entity.User;
import shootingstar.var.entity.UserType;
import shootingstar.var.repository.UserRepository;

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
}
