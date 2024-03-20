package shootingstar.var.Service;

import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import shootingstar.var.entity.PaymentsInfo;
import shootingstar.var.entity.User;
import shootingstar.var.jwt.JwtTokenProvider;
import shootingstar.var.repository.PaymentRepository;
import shootingstar.var.repository.UserRepository;

import java.util.Optional;
import java.util.UUID;

@Service
public class PaymentService {
    private PaymentRepository paymentRepository;
    private JwtTokenProvider jwtTokenProvider;



    public void verifyIamportService(IamportResponse<Payment> irsp, Long amount, String accessToken) {
        Authentication authentication = jwtTokenProvider.getAuthenticationFromAccessToken(accessToken);
        String userUuid = authentication.getName();
        //User user = findUserByUUID(userUuid);

        if (irsp.getResponse().getAmount().longValue() != amount) {
            throw new RuntimeException();
        }

        //PaymentsInfo paymentsInfo = new PaymentsInfo("UUID", amount, "");
    }



}
