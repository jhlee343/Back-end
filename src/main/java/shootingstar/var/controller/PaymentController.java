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
    public IamportResponse<Payment> paymentComplete(HttpServletRequest request, @RequestBody Map<String, String> map) throws IamportResponseException, IOException {
        String impUid = map.get("imp_uid");
        long actionBoardNo = Long.parseLong(map.get("actionBoardNo"));
        int amount = Integer.parseInt(map.get("amount"));

        IamportResponse<Payment> irsp = paymentLookup(impUid);

        //paymentService.verifyIamportService(irsp, amount, actionBoardNo);

        return irsp;
    }
}
