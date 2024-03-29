package shootingstar.var.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import shootingstar.var.repository.user.UserRepository;

@Service
@RequiredArgsConstructor
public class CheckDuplicateService {
    private final UserRepository userRepository;

    public boolean checkNicknameDuplicate(String nickname) {
        return userRepository.existsByNicknameAndIsWithdrawn(nickname, false);
    }

    public boolean checkEmailDuplicate(String email) {
        return userRepository.existsByEmailAndIsWithdrawn(email, false);
    }
}
