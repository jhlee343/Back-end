package shootingstar.var.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shootingstar.var.Service.AdminService;
import shootingstar.var.dto.req.AdminLoginReqDto;
import shootingstar.var.dto.req.AdminSignupReqDto;
import shootingstar.var.dto.res.*;
import shootingstar.var.jwt.TokenInfo;
import shootingstar.var.jwt.TokenProperty;
import shootingstar.var.util.TokenUtil;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {
    private final AdminService adminService;
    private final TokenProperty tokenProperty;

    @PostMapping("/signup")
    public void signup(@RequestBody AdminSignupReqDto reqDto) {
        adminService.signup(reqDto.getLoginId(), reqDto.getPassword(), reqDto.getSecretKey());
    }

    @PostMapping("/login")
    public void login(@RequestBody AdminLoginReqDto reqDto, HttpServletResponse response) {
        TokenInfo tokenInfo = adminService.login(reqDto.getLoginId(), reqDto.getPassword());
        TokenUtil.addHeader(response, tokenInfo.getAccessToken());
        TokenUtil.updateCookie(response, tokenInfo.getRefreshToken(), tokenProperty.getREFRESH_EXPIRE());
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
    public ResponseEntity<String> warning(@PathVariable String userUUID) {
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
    public ResponseEntity<AllVipInfosDto> getVipInfoDetail(@PathVariable("vipInfoUUID") String vipInfoUUID, HttpServletRequest request) {
        AllVipInfosDto vipInfoDetail = adminService.getVipInfoDetail(vipInfoUUID);
        return ResponseEntity.ok().body(vipInfoDetail);
    }

    @PatchMapping("/vip/changeState/{vipInfoUUID}")
    public ResponseEntity<String> vipInfoChange(
            @PathVariable("vipInfoUUID") String vipInfoUUID,
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
