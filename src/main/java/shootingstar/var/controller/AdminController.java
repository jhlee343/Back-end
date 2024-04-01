package shootingstar.var.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import shootingstar.var.Service.AdminService;
import shootingstar.var.dto.req.AdminSignupReqDto;
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
    public void login(@RequestBody AdminSignupReqDto reqDto, HttpServletResponse response) {
        TokenInfo tokenInfo = adminService.login(reqDto.getLoginId(), reqDto.getPassword());
        TokenUtil.addHeader(response, tokenInfo.getAccessToken());
        TokenUtil.updateCookie(response, tokenInfo.getRefreshToken(), tokenProperty.getREFRESH_EXPIRE());
    }

    @GetMapping("/test")
    public String test() {
        return "관리자 접근 성공";
    }
}
