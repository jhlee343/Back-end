package shootingstar.var.controller;

import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shootingstar.var.Service.PaymentService;
import shootingstar.var.dto.req.ExchangeReqDto;
import shootingstar.var.dto.req.PaymentReqDto;
import shootingstar.var.exception.CustomException;
import shootingstar.var.exception.ErrorCode;
import shootingstar.var.exception.ErrorResponse;
import shootingstar.var.jwt.JwtTokenProvider;

import java.io.IOException;

@Tag(name = "결제 컨트롤러")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PaymentController {
    private final PaymentService paymentService;
    private final JwtTokenProvider jwtTokenProvider;

    @Operation(summary = "포인트 결제 API", description = "포인트 결제 데이터를 PortOne서버의 데이터와 비교, 검증하는 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "포인트 결제 성공",
                    content = { @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class)) }),
            @ApiResponse(responseCode = "401",
                    description = "포트원 ACCESS 토큰 발급 실패: 3102\n",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) }),
            @ApiResponse(responseCode = "403",
                    description = "결제 금액 위변조: 3100\n",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) }),
            @ApiResponse(responseCode = "404",
                    description = "존재하지 않는 사용자: 1201\n" +
                            "존재하지 않는 거래내역: 3200\n" +
                            "존재하지 않는 지갑: 10200\n",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) }),
            @ApiResponse(responseCode = "500",
                    description = "결제 서버 응답 오류: 3001\n" +
                            "결제 서버 연결 실패: 3002\n",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) }),
    })
    @PostMapping("/payment/point")
    public ResponseEntity<String> pointPayment(HttpServletRequest request, @RequestBody PaymentReqDto paymentReqDto) {
        String userUUID = jwtTokenProvider.getUserUUIDByRequest(request);

        paymentService.verifyPointPayment(paymentReqDto, userUUID);

        return ResponseEntity.ok("포인트 충전이 완료되었습니다.");
    }

    @Operation(summary = "구독 결제 API", description = "구독 결제 데이터를 PortOne서버의 데이터와 비교, 검증하는 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "구독 결제 성공", content = {
                    @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class)) }),
            @ApiResponse(responseCode = "401",
                    description = "포트원 ACCESS 토큰 발급 실패: 3102\n",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) }),
            @ApiResponse(responseCode = "403",
                    description = "결제 금액 위변조: 3100\n",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) }),
            @ApiResponse(responseCode = "404",
                    description = "존재하지 않는 사용자: 1201\n" +
                            "존재하지 않는 거래내역: 3200\n",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) }),
            @ApiResponse(responseCode = "500",
                    description = "결제 서버 응답 오류: 3001\n" +
                            "결제 서버 연결 실패: 3002\n",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) }),
    })
    @PostMapping("/payment/subscribe")
    public ResponseEntity<String> subscribePayment(HttpServletRequest request, @RequestBody PaymentReqDto paymentReqDto) {
        String userUUID = jwtTokenProvider.getUserUUIDByRequest(request);

        paymentService.verifySubscribePayment(paymentReqDto, userUUID);

        return ResponseEntity.ok("구독이 완료되었습니다.");
    }

    @Operation(summary = "포인트 환전 신청 API", description = "보유 포인트 내에서 현금으로 환전 신청하는 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "포인트 환전 신청 성공", content = {
                    @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class)) }),
            @ApiResponse(responseCode = "400",
                    description = "잘못된 포인트 값: 3000\n",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) }),
            @ApiResponse(responseCode = "403",
                    description = "계좌 명의 불일치: 3101\n",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) }),
            @ApiResponse(responseCode = "404",
                    description = "존재하지 않는 사용자: 1201\n",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) }),
    })
    @PostMapping("/payment/exchange")
    public ResponseEntity<String> applyExchange(HttpServletRequest request, @RequestBody ExchangeReqDto exchangeReqDto) {
        String userUUID = jwtTokenProvider.getUserUUIDByRequest(request);
        paymentService.applyExchange(exchangeReqDto, userUUID);
        return ResponseEntity.ok("포인트 환전 신청이 완료되었습니다.");
    }
}
