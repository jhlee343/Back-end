package shootingstar.var.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import shootingstar.var.Service.AuctionService;
import shootingstar.var.dto.req.AuctionCreateReqDto;
import shootingstar.var.dto.req.AuctionReportReqDto;
import shootingstar.var.enums.type.UserType;
import shootingstar.var.exception.ErrorResponse;
import shootingstar.var.jwt.JwtTokenProvider;

@Slf4j
@Tag(name = "경매 컨트롤러", description= "경매 생성과 경매 취소는 vip 권한만 사용 가능, "
                                    + "경매 신고는 basic, vip 둘 다 사용 가능")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AuctionController {

    private final AuctionService auctionService;
    private final JwtTokenProvider jwtTokenProvider;

    @Operation(summary = "경매 생성 API", description = "VIP 권한을 가진 경매 주최자가 경매를 생성할 때 사용")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "- 사용자 타입이 VIP일 때 : 경매 생성 성공"),
            @ApiResponse(responseCode = "404",
                    description = "- 사용자 정보 조회 실패 : 1201",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "400",
                    description =
                                    "- 보유 포인트보다 큰 최소 입찰 금액 입력 시 : 2000\n" +
                                    "- 10만원보다 적은 금액을 입력하거나 만원 단위가 아닌 최소 입찰 금액 입력 시 : 2001\n" +
                                    "- 현재 날짜로부터 30일 이후의 날짜를 입력하지 않거나 잘못된 형식의 식사 날짜 입력 시 : 2002\n" +
                                    "- 잘못된 형식의 식사 장소 입력 시 : 2003\n" +
                                    "- 잘못된 형식의 만남에 대한 정보 입력 시 : 2004\n" +
                                    "- 잘못된 형식의 만남에 대한 약속 입력 시 : 2005\n",
                                            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "500",
                    description = "- 스케줄링 실패 : 4000",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @PostMapping("/vip/auction/create")
    public ResponseEntity<String> create(@Valid @RequestBody AuctionCreateReqDto reqDto, HttpServletRequest request) {
        String userUUID = jwtTokenProvider.getUserUUIDByRequest(request);
        auctionService.create(reqDto, userUUID);
        return ResponseEntity.ok().body("경매 생성 성공");
    }

    @Operation(summary = "경매 취소 API", description = "VIP 권한을 가진 경매 주최자가 경매를 취소할 때 사용")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "- 사용자 타입이 VIP일 때 : 경매 취소 성공"),
            @ApiResponse(responseCode = "404",
                    description =
                                    "- 사용자 정보 조회 실패 : 1201\n" +
                                    "- 경매가 존재하지 않을 때 : 2200\n" +
                                    "- 스케줄링된 task가 존재하지 않을 때 : 4200\n" +
                                    "- 스케줄링된 task를 취소하는 데 실패 했을 때 : 4201",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "403",
                    description = "- 로그인 한 사용자가 경매 주최자가 아닐 때 : 2100",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "409",
                    description = "- 경매 타입이 진행중(PROGRESS)이 아닐 때 : 2300",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "500",
                    description = "- 스케줄링 실패 : 4000",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @PatchMapping("/vip/auction/cancel/{auctionUUID}")
    public ResponseEntity<String> cancel(@PathVariable("auctionUUID") String auctionUUID, HttpServletRequest request) {
        String userUUID = jwtTokenProvider.getUserUUIDByRequest(request);
        auctionService.cancel(auctionUUID, userUUID, UserType.ROLE_VIP.toString());
        return ResponseEntity.ok().body("경매 취소 성공");
    }

    @Operation(summary = "경매 신고 API", description = "경매 진행중 일 때 BASIC, VIP 사용 가능")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "- 사용자 타입이 BASIC, VIP일 때 : 경매 신고 성공"),
            @ApiResponse(responseCode = "404",
                    description =
                                    "- 사용자 정보 조회 실패 : 1201\n" +
                                    "- 경매가 존재하지 않을 때 : 2200",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "409",
                    description = "- 경매 타입이 진행중(PROGRESS)이 아닐 때 : 2300",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @PostMapping("/auction/report")
    public ResponseEntity<String> reportAuction(@Valid @RequestBody AuctionReportReqDto reqDto, HttpServletRequest request) {
        String userUUID = jwtTokenProvider.getUserUUIDByRequest(request);
        auctionService.reportAuction(reqDto, userUUID);
        return ResponseEntity.ok().body("경매 신고 성공");
    }
}
