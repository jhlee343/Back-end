package shootingstar.var.Service;

import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shootingstar.var.dto.req.ExchangeReqDto;
import shootingstar.var.entity.Exchange;
import shootingstar.var.entity.PaymentsInfo;
import shootingstar.var.entity.User;
import shootingstar.var.exception.CustomException;
import shootingstar.var.exception.ErrorCode;
import shootingstar.var.repository.exchange.ExchangeRepository;
import shootingstar.var.repository.payment.PaymentRepository;
import shootingstar.var.repository.UserRepository;

import static shootingstar.var.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final ExchangeRepository exchangeRepository;



    @Transactional
    public void verifyPointPayment(IamportResponse<Payment> ires, Long amount, String userUUID) {
        User user = userRepository.findByUserUUID(userUUID)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (ires.getResponse().getAmount().longValue() != amount) {
            throw new CustomException(PAYMENT_ACCESS_DENIED);
        }

        PaymentsInfo paymentsInfo = new PaymentsInfo(user, amount);
        paymentRepository.save(paymentsInfo);

        user.increasePoint(BigDecimal.valueOf(amount));
    }

    @Transactional
    public void verifySubscribePayment(IamportResponse<Payment> ires, Long amount, String userUUID) {
        User user = userRepository.findByUserUUID(userUUID)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (ires.getResponse().getAmount().longValue() != amount) {
            throw new CustomException(PAYMENT_ACCESS_DENIED);
        }

        PaymentsInfo paymentsInfo = new PaymentsInfo(user, amount);
        paymentRepository.save(paymentsInfo);

        user.subscribeActivate();
    }

    @Transactional
    public void applyExchange(ExchangeReqDto exchangeReqDto, String userUUID) {
        User user = userRepository.findByUserUUID(userUUID)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (!exchangeReqDto.getExchangeAccountHolder().equals(user.getName())) {
            throw new CustomException(DIFFERENT_ACCOUNT_HOLDER);
        }

        if (new BigDecimal(exchangeReqDto.getExchangePoint()).compareTo(user.getPoint()) == 1) {
            throw new CustomException(EXCHANGE_AMOUNT_INCORRECT_FORMAT);
        }

        Exchange exchange = Exchange.builder()
                .user(user)
                .exchangePoint(exchangeReqDto.getExchangePoint())
                .exchangeAccount(exchangeReqDto.getExchangeAccount())
                .exchangeBank(exchangeReqDto.getExchangeBank())
                .exchangeAccountHolder(exchangeReqDto.getExchangeAccountHolder())
                .build();

        exchangeRepository.save(exchange);

        user.decreasePoint(BigDecimal.valueOf(exchangeReqDto.getExchangePoint()));
    }
}
