package shootingstar.var.Service;

import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shootingstar.var.dto.req.ExchangeReqDto;
import shootingstar.var.dto.req.PaymentReqDto;
import shootingstar.var.entity.*;
import shootingstar.var.enums.type.PointOriginType;
import shootingstar.var.exception.CustomException;
import shootingstar.var.exception.ErrorCode;
import shootingstar.var.repository.PointLogRepository;
import shootingstar.var.repository.exchange.ExchangeRepository;
import shootingstar.var.repository.payment.PaymentRepository;
import shootingstar.var.repository.user.UserRepository;
import shootingstar.var.repository.wallet.WalletRepository;

import static shootingstar.var.exception.ErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final ExchangeRepository exchangeRepository;
    private final PointLogRepository pointLogRepository;
    private final WalletRepository walletRepository;



    @Transactional
    public void verifyPointPayment(IamportResponse<Payment> ires, PaymentReqDto paymentReqDto, String userUUID) {
        User user = userRepository.findByUserUUIDWithPessimisticLock(userUUID)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Wallet wallet = walletRepository.findWithPessimisticLock()
                .orElseThrow(() -> new CustomException(ErrorCode.WALLET_NOT_FOUND));

        if (ires.getResponse().getAmount().longValue() != paymentReqDto.getPaymentAmount()) {
            throw new CustomException(PAYMENT_ACCESS_DENIED);
        }

        PaymentsInfo paymentsInfo = new PaymentsInfo(user, paymentReqDto.getPaymentAmount());
        paymentRepository.save(paymentsInfo);

        log.info("사용자 포인트 충전 전 포인트: {}", user.getPoint());
        user.increasePoint(BigDecimal.valueOf(paymentReqDto.getPaymentAmount()));
        log.info("사용자 포인트 충전 후 포인트: {}", user.getPoint());

        log.info("사용자 포인트 충전 전 Wallet: {}", wallet.getCurrentCash());
        wallet.increaseCash(BigDecimal.valueOf(paymentReqDto.getPaymentAmount()));
        log.info("사용자 포인트 충전 후 Wallet: {}", wallet.getCurrentCash());

        PointLog pointLog = PointLog.createPointLogWithDeposit(user, PointOriginType.CHARGE, BigDecimal.valueOf(paymentReqDto.getPaymentAmount()));
        pointLogRepository.save(pointLog);
    }

    @Transactional
    public void verifySubscribePayment(IamportResponse<Payment> ires, PaymentReqDto paymentReqDto, String userUUID) {
        User user = userRepository.findByUserUUID(userUUID)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (ires.getResponse().getAmount().longValue() != paymentReqDto.getPaymentAmount()) {
            throw new CustomException(PAYMENT_ACCESS_DENIED);
        }

        PaymentsInfo paymentsInfo = new PaymentsInfo(user, paymentReqDto.getPaymentAmount());
        paymentRepository.save(paymentsInfo);

        user.subscribeActivate();
    }

    @Transactional
    public void applyExchange(ExchangeReqDto exchangeReqDto, String userUUID) {
        User user = userRepository.findByUserUUIDWithPessimisticLock(userUUID)
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

        log.info("사용자 환전 신청 전 포인트: {}", user.getPoint());
        user.decreasePoint(BigDecimal.valueOf(exchangeReqDto.getExchangePoint()));
        log.info("사용자 환전 신청 후 포인트: {}", user.getPoint());

        PointLog pointLog = PointLog.createPointLogWithWithdrawal(user, PointOriginType.EXCHANGE, BigDecimal.valueOf(exchangeReqDto.getExchangePoint()));
        pointLogRepository.save(pointLog);
    }
}
