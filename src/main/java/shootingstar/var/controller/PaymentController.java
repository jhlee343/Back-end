package shootingstar.var.controller;

import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shootingstar.var.Service.PaymentService;
import shootingstar.var.dto.req.PaymentReqDto;
import shootingstar.var.exception.CustomException;
import shootingstar.var.exception.ErrorCode;

import java.io.IOException;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PaymentController {
    private final PaymentService paymentService;

    private IamportClient iamportClient;

    @Value("#{environment['imp.api.key']}")
    private String apiKey;

    @Value("#{environment['imp.api.secretkey']}")
    private String secretKey;

    @PostConstruct
    public void init() {
        this.iamportClient = new IamportClient(apiKey, secretKey);
    }

    public IamportResponse<Payment> paymentLookup(String impUid) throws IamportResponseException, IOException {
        return iamportClient.paymentByImpUid(impUid);
    }



    @PostMapping("/payment")
    public IamportResponse<Payment> paymentComplete(HttpServletRequest request, @RequestBody PaymentReqDto paymentReqDto) throws IamportResponseException, IOException {
        String accessToken = getTokenFromHeader(request);

        Long amount = paymentReqDto.getAmount();

        IamportResponse<Payment> irsp = paymentLookup(paymentReqDto.getImp_uid());

        paymentService.verifyIamportService(irsp, amount, accessToken);

        return irsp;
    }


    private static String getTokenFromHeader(HttpServletRequest request) {
        String token = request.getHeader("Authorization"); // 헤더에 존재하는 엑세스 토큰을 받아온다.

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7); // "Bearer " 접두어 제거
        } else {
            throw new CustomException(ErrorCode.INVALID_ACCESS_TOKEN);
        }
        return token;
    }
}
