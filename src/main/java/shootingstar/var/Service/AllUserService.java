package shootingstar.var.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import shootingstar.var.dto.req.UserSignupReqDto;
import shootingstar.var.entity.User;
import shootingstar.var.entity.UserType;
import shootingstar.var.exception.CustomException;
import shootingstar.var.exception.ErrorCode;
import shootingstar.var.repository.UserRepository;
import shootingstar.var.util.MailRedisUtil;

@Service
@RequiredArgsConstructor
public class AllUserService {
    private final UserRepository userRepository;
    private final MailRedisUtil mailRedisUtil;
    private final CheckDuplicateService duplicateService;

    public void signup(UserSignupReqDto reqDto) {
        if (duplicateService.checkEmailDuplicate(reqDto.getEmail())) {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }
        if (duplicateService.checkNicknameDuplicate(reqDto.getNickname())) {
            throw new CustomException(ErrorCode.DUPLICATE_NICKNAME);
        }

        if (mailRedisUtil.hasKey(reqDto.getEmail()) && mailRedisUtil.getData(reqDto.getEmail()).equals("validate")) { // 이메일 인증을 받은 이메일 인지 확인

            User user = User.builder()
                    .kakaoId(reqDto.getKakaoId())
                    .name(reqDto.getUserName())
                    .nickname(reqDto.getNickname())
                    .phone(reqDto.getPhoneNumber())
                    .email(reqDto.getEmail())
                    .profileImgUrl(reqDto.getProfileImgUrl())
                    .userType(UserType.ROLE_BASIC)
                    .build();

            userRepository.save(user);
            mailRedisUtil.deleteData(reqDto.getEmail());
        } else {
            throw new CustomException(ErrorCode.VALIDATE_ERROR_EMAIL);
        }
    }
}
