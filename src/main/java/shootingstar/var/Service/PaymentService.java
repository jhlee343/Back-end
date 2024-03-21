package shootingstar.var.Service;

import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import shootingstar.var.entity.PaymentsInfo;
import shootingstar.var.entity.User;
import shootingstar.var.exception.CustomException;
import shootingstar.var.jwt.JwtTokenProvider;
import shootingstar.var.repository.PaymentRepository;
import shootingstar.var.repository.UserRepository;

import java.util.Optional;
import java.util.UUID;

import static shootingstar.var.exception.ErrorCode.INCORRECT_FORMAT;
import static shootingstar.var.exception.ErrorCode.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;



    public void verifyIamportService(IamportResponse<Payment> ires, Long amount, String accessToken) {
        Authentication authentication = jwtTokenProvider.getAuthenticationFromAccessToken(accessToken);
        String userUuid = authentication.getName();
        User user = findUserByUUID(userUuid);

        if (ires.getResponse().getAmount().longValue() != amount) {
            throw new CustomException(INCORRECT_FORMAT);
        }

        PaymentsInfo paymentsInfo = new PaymentsInfo(amount, user);
        paymentRepository.save(paymentsInfo);
    }



    private User findUserByUUID(String userUuid) {
        Optional<User> optionalUser = userRepository.findByUserUUID(userUuid);
        if (optionalUser.isEmpty()) {
            // 엑세스 토큰을 통해 사용자를 찾지 못했을 때
            // 이 오류가 발생한다면 이미 탈퇴한 회원이 만료되지 않은 엑세스 토큰을 통해 비밀번호 확인을 시도했거나
            // 어떠한 방법으로 JWT 토큰의 사용자 고유번호를 변경했을 때
            throw new CustomException(USER_NOT_FOUND);
        }
        return optionalUser.get();
    }
}
