package shootingstar.var.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import shootingstar.var.Service.UserAuthService;
import shootingstar.var.dto.req.AccessKakaoReqDto;
import shootingstar.var.dto.res.AccessKakaoResDto;
import shootingstar.var.dto.res.KakaoUserResDto;
import shootingstar.var.jwt.JwtTokenProvider;
import shootingstar.var.jwt.TokenInfo;
import shootingstar.var.jwt.TokenProperty;
import shootingstar.var.oAuth.KakaoAPI;
import shootingstar.var.oAuth.KakaoUserInfo;
import shootingstar.var.util.TokenUtil;

@Controller
@RequiredArgsConstructor
public class LoginController {

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/oauth2/redirect")
    public String redirect() {
        return "redirect";
    }
}