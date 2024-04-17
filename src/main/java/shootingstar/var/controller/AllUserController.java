package shootingstar.var.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import shootingstar.var.Service.AllUserService;
import shootingstar.var.Service.CheckDuplicateService;
import shootingstar.var.Service.EmailService;
import shootingstar.var.dto.req.CheckAuthCodeReqDto;
import shootingstar.var.dto.req.SendAuthCodeReqDto;
import shootingstar.var.dto.req.UserSignupReqDto;
import shootingstar.var.dto.res.*;
import shootingstar.var.enums.type.AuctionSortType;
import shootingstar.var.exception.ErrorResponse;
import shootingstar.var.util.TokenUtil;

import java.math.BigDecimal;
import java.util.List;

@Tag(name = "AllUserController", description = "로그인하지 않아도 접속 가능한 컨트롤러")
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/all")
public class AllUserController {

    private final AllUserService allUserService;
    private final EmailService emailService;
    private final CheckDuplicateService duplicateService;

    @Operation(summary = "회원가입 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원가입 성공", content = {
                    @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))}),
            @ApiResponse(responseCode = "400",
                    description =
                            "- 잘못된 형식의 이메일 : 1001\n" +
                                    "- 잘못된 형식의 닉네임 : 1003\n" +
                                    "- 잘못된 형식의 카카오 고유번호 : 1004\n" +
                                    "- 잘못된 형식의 사용자 이름 : 1005\n" +
                                    "- 잘못된 형식의 휴대폰 번호 : 1006\n" +
                                    "- 잘못된 형식의 프로필 이미지 주소 : 1007",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 이메일로 회원가입 시도 : 1102",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "409",
                    description =
                            "- 이미 사용중인 이메일로 회원가입 시도 : 1301\n" +
                                    "- 이미 사용중인 닉네임으로 회원가입 시도 : 1302",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@Valid @RequestBody UserSignupReqDto reqDto) {
        allUserService.signup(reqDto);
        return ResponseEntity.ok().body("회원가입에 성공하였습니다.");
    }

    @Operation(summary = "닉네임 중복검사 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "중복 닉네임인 경우 true, 사용가능한 닉네임인 경우 false 를 반환", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Boolean.class))}),
            @ApiResponse(responseCode = "400", description = "잘못된 형식의 닉네임 : 1003", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @GetMapping("/duplicate/nickname/{nickname}")
    public ResponseEntity<Boolean> checkNicknameDuplicate(@NotBlank @PathVariable("nickname") String nickname) {
        return ResponseEntity.ok(duplicateService.checkNicknameDuplicate(nickname));
    }

    @Operation(summary = "이메일 중복검사 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "중복 이메일인 경우 true, 사용가능한 이메일인 경우 false 를 반환", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Boolean.class))}),
            @ApiResponse(responseCode = "400", description = "잘못된 형식의 이메일 : 1001", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @GetMapping("/duplicate/email/{email}")
    public ResponseEntity<Boolean> checkEmailDuplicate(@NotBlank @PathVariable("email") String email) {
        return ResponseEntity.ok(duplicateService.checkEmailDuplicate(email));
    }

    @Operation(summary = "인증 이메일 발송 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "이메일 전송에 성공하였을 때", content = {
                    @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))}),
            @ApiResponse(responseCode = "400", description = "잘못된 형식의 이메일 : 1001", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @PostMapping("/email/sendAuthCode")
    public ResponseEntity<String> sendAuthCode(@Valid @RequestBody SendAuthCodeReqDto reqDto) {
        emailService.sendAuthCodeEmail(reqDto.getEmail());
        return ResponseEntity.ok().body("인증코드를 발송하였습니다.");
    }

    @Operation(summary = "이메일 인증 코드 검증 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "인증 코드 검증에 성공하였을 때", content = {
                    @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))}),
            @ApiResponse(responseCode = "400", description = "잘못된 형식의 이메일 : 1001", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "401", description = "잘못된 인증 코드 : 1101", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @PostMapping("/email/checkAuthCode")
    public ResponseEntity<String> checkAuthCode(@Valid @RequestBody CheckAuthCodeReqDto reqDto) {
        emailService.validateCode(reqDto.getEmail(), reqDto.getCode());
        return ResponseEntity.ok().body("이메일 인증에 성공하였습니다.");
    }

    @Operation(summary = "배너 전체 조회 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "배너 전체 조회, 리스트 타입", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = GetBannerResDto.class))}),
    })
    @GetMapping("/banner")
    public ResponseEntity<List<GetBannerResDto>> getBanner() {
        List<GetBannerResDto> banners = allUserService.getBanner();
        return ResponseEntity.ok().body(banners);
    }

    @Operation(summary = "현재 전체 기부금액 조회 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "현재 전체 기부금액 조회", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = TotalDonationPriceResDto.class))}),
            @ApiResponse(responseCode = "404", description = "지갑을 찾을 수 없음 : 10200", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @GetMapping("/totalDonation")
    public ResponseEntity<TotalDonationPriceResDto> getTotalDonation() {
        BigDecimal totalDonation = allUserService.getTotalDonation();
        return ResponseEntity.ok().body(new TotalDonationPriceResDto(totalDonation));
    }

    @Operation(summary = "모든 사용자가 접근 가능한 VIP 리스트 조회 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "VIP 리스트 조회", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = VipListResDto.class))}),
            @ApiResponse(responseCode = "404",
                    description = "액세스 토큰 전달 시 존재하지 않는 사용자 : 1201",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @GetMapping("/vipList")
    public ResponseEntity<Page<VipListResDto>> vipList(@PageableDefault(size = 10) Pageable pageable, @RequestParam(value = "search", required = false) String search, HttpServletRequest request) {
        String accessToken = TokenUtil.getTokenFromHeader(request);
        Page<VipListResDto> vipList = allUserService.getVipList(pageable, search, accessToken);
        return ResponseEntity.ok().body(vipList);
    }

    @Operation(summary = "모든 사용자가 접근 가능한 VIP 상세정보 조회 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "VIP 상세 정보 조회", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = VipDetailResDto.class))}),
            @ApiResponse(responseCode = "400",
                    description = "잘못된 형식의 사용자 고유번호 : 1008",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "404",
                    description =
                                    "- 액세스 토큰 전달 시 존재하지 않는 사용자 : 1201\n" +
                                    "- 존재하지 않는 사용자 : 1201\n" +
                                    "- 존재하지 않는 VIP 정보 : 7200",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @GetMapping("/vipDetail/{vipUUID}")
    public ResponseEntity<VipDetailResDto> vipDetail(@NotBlank @PathVariable("vipUUID") String vipUUID, HttpServletRequest request) {
        String accessToken = TokenUtil.getTokenFromHeader(request);
        VipDetailResDto vipDetail = allUserService.getVipDetail(vipUUID, accessToken);
        return ResponseEntity.ok().body(vipDetail);
    }


    @Operation(summary = "모든 사용자가 접근 가능한 VIP 진행중인 경매 조회 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "VIP 진행중인 경매 조회", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ProgressAuctionResDto.class))}),
            @ApiResponse(responseCode = "400",
                    description = "잘못된 형식의 사용자 고유번호 : 1008",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "404",
                    description =
                            "- 존재하지 않는 사용자 : 1201\n" +
                                    "- 존재하지 않는 VIP 정보 : 7200",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @GetMapping("/auction/{vipUUID}/progressList")
    public ResponseEntity<Page<ProgressAuctionResDto>> vipProgressAuction(@NotBlank @PathVariable("vipUUID") String vipUUID, @PageableDefault(size = 10) Pageable pageable) {
        Page<ProgressAuctionResDto> vipProgressAuction = allUserService.getVipProgressAuction(vipUUID, pageable);
        return ResponseEntity.ok().body(vipProgressAuction);
    }

    @Operation(summary = "모든 사용자가 접근 가능한 VIP 받은 리뷰 조회 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "VIP 받은 리뷰 조회", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = VipReceiveReviewResDto.class))}),
            @ApiResponse(responseCode = "400",
                    description = "잘못된 형식의 사용자 고유번호 : 1008",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "404",
                    description =
                                    "- 존재하지 않는 사용자 : 1201\n" +
                                    "- 존재하지 않는 VIP 정보 : 7200",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @GetMapping("/review/{vipUUID}")
    public ResponseEntity<Page<VipReceiveReviewResDto>> vipReceivedReview(@NotBlank @PathVariable("vipUUID") String vipUUID, @PageableDefault(size = 10) Pageable pageable) {
        Page<VipReceiveReviewResDto> vipReceivedReview = allUserService.getVipReceivedReview(vipUUID, pageable);
        return ResponseEntity.ok().body(vipReceivedReview);
    }

    @Operation(summary = "진행중인 일반 경매 조회 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "진행중인 일반 경매 조회 ", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ProgressAuctionResDto.class))}),
    })
    @GetMapping("/auction/generalList")
    public ResponseEntity<Page<ProgressAuctionResDto>> progressGeneralAuctionList(@PageableDefault(size = 10) Pageable pageable,
                                                @RequestParam(value = "sortType", required = false) AuctionSortType sortType,
                                                @RequestParam(value = "search", required = false) String search) {
        Page<ProgressAuctionResDto> progressGeneralAuction = allUserService.getProgressGeneralAuction(pageable, sortType, search);
        return ResponseEntity.ok().body(progressGeneralAuction);
    }

    @Operation(summary = "일반 경매 상세정보 조회 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "일반 경매 상세정보 조회", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = AuctionDetailResDto.class))}),
            @ApiResponse(responseCode = "404",
                    description = "존재하지 않는 경매 : 2200",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @GetMapping("/auction/general/{auctionUUID}")
    public ResponseEntity<AuctionDetailResDto> auctionDetail(@NotBlank @PathVariable("auctionUUID") String auctionUUID) {
        AuctionDetailResDto auctionDetail = allUserService.getAuctionDetail(auctionUUID);
        return ResponseEntity.ok().body(auctionDetail);
    }
}
