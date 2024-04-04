package shootingstar.var.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import shootingstar.var.Service.AdminService;
import shootingstar.var.dto.req.AdminLoginReqDto;
import shootingstar.var.dto.req.AdminSignupReqDto;
import shootingstar.var.dto.res.*;
import shootingstar.var.exception.ErrorResponse;
import shootingstar.var.jwt.TokenInfo;
import shootingstar.var.jwt.TokenProperty;
import shootingstar.var.util.TokenUtil;

@Tag(name = "AdminController", description = "관리자 컨트롤러")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/lookAtMe")
public class AdminController {
    private final AdminService adminService;
    private final TokenProperty tokenProperty;

    @Operation(summary = "관리자 회원가입 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "관리자 회원가입 성공", content = {
                    @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))}),
            @ApiResponse(responseCode = "400",
                    description =
                                    "- 잘못된 형식의 관리자 ID : 9001\n" +
                                    "- 잘못된 형식의 관리자 비밀번호 : 9002\n" +
                                    "- 잘못된 형식의 관리자 닉네임 : 9003\n" +
                                    "- 잘못된 형식의 관리자 비밀 키 : 9004",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "409",
                    description =
                                    "- 사용할 수 없는 관리자 ID : 9301\n" +
                                    "- 사용할 수 없는 관리자 닉네임 : 9302\n" +
                                    "- 잘못된 값의 관리자 비밀 키 : 9303",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@Valid @RequestBody AdminSignupReqDto reqDto) {
        adminService.signup(reqDto.getAdminLoginId(), reqDto.getAdminPassword(), reqDto.getAdminNickname(), reqDto.getAdminSecretKey());
        return ResponseEntity.ok().body("회원가입에 성공하였습니다.");
    }

    @Operation(summary = "관리자 로그인 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "관리자 로그인 성공", content = {
                    @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))}),
            @ApiResponse(responseCode = "400",
                    description =
                                    "- 잘못된 형식의 관리자 ID : 9001\n" +
                                    "- 잘못된 형식의 관리자 비밀번호 : 9002",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "403",
                    description = "로그인 실패 : 잘못된 관리자 아이디 혹은 패스워드",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody AdminLoginReqDto reqDto, HttpServletResponse response) {
        TokenInfo tokenInfo = adminService.login(reqDto.getAdminLoginId(), reqDto.getAdminPassword());
        TokenUtil.addHeader(response, tokenInfo.getAccessToken());
        TokenUtil.updateCookie(response, tokenInfo.getRefreshToken(), tokenProperty.getREFRESH_EXPIRE());
        return ResponseEntity.ok().body("로그인에 성공하였습니다.");
    }

    @GetMapping("/test")
    public String test() {
        return "관리자 접근 성공";
    }

    @GetMapping("/userList")
    public ResponseEntity<Page<AllUsersDto>> getUserList(
            @RequestParam(value = "search", required = false, defaultValue = "") String search,
            @PageableDefault(size = 10) Pageable pageable, HttpServletRequest request) {
        Page<AllUsersDto> userList = adminService.getAllUsers(search, pageable);
        return ResponseEntity.ok().body(userList);
    }

    @PatchMapping("/warning/{userUUID}")
    public ResponseEntity<String> warning(@NotBlank @PathVariable String userUUID) {
        adminService.warning(userUUID);
        return ResponseEntity.ok().body("해당 회원이 경고되었습니다.");
    }

    @GetMapping("/vip/requestList")
    public ResponseEntity<Page<AllVipInfosDto>> getVipInfoList(
            @RequestParam(value = "search", required = false, defaultValue = "") String search,
            @PageableDefault(size = 10) Pageable pageable, HttpServletRequest request) {
        Page<AllVipInfosDto> vipInfoList = adminService.getAllVipInfos(search, pageable);
        return ResponseEntity.ok().body(vipInfoList);
    }

    @GetMapping("/vip/requestDetail/{vipInfoUUID}")
    public ResponseEntity<AllVipInfosDto> getVipInfoDetail(@NotBlank @PathVariable("vipInfoUUID") String vipInfoUUID, HttpServletRequest request) {
        AllVipInfosDto vipInfoDetail = adminService.getVipInfoDetail(vipInfoUUID);
        return ResponseEntity.ok().body(vipInfoDetail);
    }

    @PatchMapping("/vip/changeState/{vipInfoUUID}")
    public ResponseEntity<String> vipInfoChange(
            @NotBlank @PathVariable("vipInfoUUID") String vipInfoUUID,
            @RequestParam String vipInfoState
    ) {
        adminService.vipInfoChange(vipInfoUUID, vipInfoState);
        return ResponseEntity.ok().body("VIP 신청 결과");
    }

    @GetMapping("/ticketList")
    public ResponseEntity<Page<AllTicketsDto>> getTicketList(
            @RequestParam(value = "search", required = false, defaultValue = "") String search,
            @PageableDefault(size = 10) Pageable pageable, HttpServletRequest request) {
        Page<AllTicketsDto> ticketList = adminService.getAllTickets(search, pageable);
        return ResponseEntity.ok().body(ticketList);
    }

    @GetMapping("/reviewList")
    public ResponseEntity<Page<AllReviewsDto>> getReviewList(
            @RequestParam(value = "search", required = false, defaultValue = "") String search,
            @PageableDefault(size = 10) Pageable pageable, HttpServletRequest request) {
        Page<AllReviewsDto> reviewList = adminService.getAllReviews(search, pageable);
        return ResponseEntity.ok().body(reviewList);
    }

    @GetMapping("/exchange/requestList")
    public ResponseEntity<Page<AllExchangesDto>> getExchangeList(
            @RequestParam(value = "search", required = false, defaultValue = "") String search,
            @PageableDefault(size = 10) Pageable pageable, HttpServletRequest request) {
        Page<AllExchangesDto> exchangeList = adminService.getAllExchanges(search, pageable);
        return ResponseEntity.ok().body(exchangeList);
    }

}
