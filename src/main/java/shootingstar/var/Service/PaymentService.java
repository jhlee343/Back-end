package shootingstar.var.Service;

import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.CancelData;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;

import java.io.IOException;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shootingstar.var.dto.req.ExchangeReqDto;
import shootingstar.var.dto.req.PaymentReqDto;
import shootingstar.var.entity.*;
import shootingstar.var.entity.log.PointLog;
import shootingstar.var.enums.type.PointOriginType;
import shootingstar.var.exception.CustomException;
import shootingstar.var.exception.ErrorCode;
import shootingstar.var.repository.log.PointLogRepository;
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
    private final IamportClient iamportClient;



    @Transactional
    public void verifyPointPayment(PaymentReqDto paymentReqDto, String userUUID) {
        User user = userRepository.findByUserUUIDWithPessimisticLock(userUUID)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Wallet wallet = walletRepository.findWithPessimisticLock()
                .orElseThrow(() -> new CustomException(ErrorCode.WALLET_NOT_FOUND));

        try {
            // 결제 단건 조회(포트원)
            IamportResponse<Payment> iamportResponse = iamportClient.paymentByImpUid(paymentReqDto.getImp_uid());

            // 결제 금액 검증
            // 결제 금액 위변조로 의심되는 결제 내역 취소(포트원)
            if (iamportResponse.getResponse().getAmount().longValue() != paymentReqDto.getPaymentAmount()) {
                iamportClient.cancelPaymentByImpUid(new CancelData(iamportResponse.getResponse().getImpUid(), true,
                                                    BigDecimal.valueOf(iamportResponse.getResponse().getAmount().longValue())));
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

        } catch (IamportResponseException e) {
            log.info(e.getMessage());

            switch (e.getHttpStatusCode()) {
                case 401:
                    throw new CustomException(PORTONE_AUTHENTICATION_ERROR);
                case 404:
                    throw new CustomException(PORTONE_PAYMENT_NOT_FOUND);
                case 500:
                    throw new CustomException(PORTONE_SERVER_RESPONSE_ERROR);
            }
        } catch (IOException e) {
            log.info(e.getMessage());
            throw new CustomException(PORTONE_SERVER_DISCONNECTED);
        }
    }

    @Transactional
    public void verifySubscribePayment(PaymentReqDto paymentReqDto, String userUUID) {
        User user = userRepository.findByUserUUID(userUUID)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        try {
            // 결제 단건 조회(포트원)
            IamportResponse<Payment> iamportResponse = iamportClient.paymentByImpUid(paymentReqDto.getImp_uid());

            // 결제 금액 검증
            // 결제 금액 위변조로 의심되는 결제 내역 취소(포트원)
            if (iamportResponse.getResponse().getAmount().longValue() != paymentReqDto.getPaymentAmount()) {
                iamportClient.cancelPaymentByImpUid(new CancelData(iamportResponse.getResponse().getImpUid(), true,
                                                    BigDecimal.valueOf(iamportResponse.getResponse().getAmount().longValue())));
                throw new CustomException(PAYMENT_ACCESS_DENIED);
            }

            PaymentsInfo paymentsInfo = new PaymentsInfo(user, paymentReqDto.getPaymentAmount());
            paymentRepository.save(paymentsInfo);

            user.subscribeActivate();

        } catch (IamportResponseException e) {
            log.info(e.getMessage());

            switch (e.getHttpStatusCode()) {
                case 401:
                    throw new CustomException(PORTONE_AUTHENTICATION_ERROR);
                case 404:
                    throw new CustomException(PORTONE_PAYMENT_NOT_FOUND);
                case 500:
                    throw new CustomException(PORTONE_SERVER_RESPONSE_ERROR);
            }
        } catch (IOException e) {
            log.info(e.getMessage());
            throw new CustomException(PORTONE_SERVER_DISCONNECTED);
        }
    }

    @Transactional
    public void applyExchange(ExchangeReqDto exchangeReqDto, String userUUID) {
        User user = userRepository.findByUserUUIDWithPessimisticLock(userUUID)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 본인 명의의 계좌가 아닐 경우
        if (!exchangeReqDto.getExchangeAccountHolder().equals(user.getName())) {
            throw new CustomException(DIFFERENT_ACCOUNT_HOLDER);
        }

        // 환전 신청 포인트가 보유 포인트를 초과했을 경우
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
