package shootingstar.var.controller;

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

    private final KakaoAPI kakaoAPI;
    private final UserAuthService userAuthService;
    private final JwtTokenProvider tokenProvider;
    private final TokenProperty tokenProperty;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/oauth2/redirect")
    public String redirect() {
        return "redirect";
    }

    @ResponseBody
    @PostMapping("/oauth2/accessKakao")
    public ResponseEntity<AccessKakaoResDto> accessKakao(@RequestBody AccessKakaoReqDto reqDto, HttpServletRequest request, HttpServletResponse response) {
        String accessTokenFromKakao = kakaoAPI.getAccessTokenFromKakao(reqDto.getCode());
        KakaoUserInfo kakaoUserInfo = kakaoAPI.getUserInfoFromKakao(accessTokenFromKakao);

        Authentication authentication = userAuthService.loadUserByKakaoId(kakaoUserInfo.getProviderId());

        if (authentication == null) {
            KakaoUserResDto kakaoUserResDto = new KakaoUserResDto(
                    kakaoUserInfo.getProviderId(),
                    kakaoUserInfo.getName(),
                    kakaoUserInfo.getEmail(),
                    kakaoUserInfo.getPhoneNumber(),
                    kakaoUserInfo.getProfileImgUrl());

            return ResponseEntity.ok().body(new AccessKakaoResDto("JOIN", kakaoUserResDto));
        } else {
            String oldRefreshToken = TokenUtil.getTokenFromCookie(request);

            if (oldRefreshToken != null) tokenProvider.expiredRefreshToken(oldRefreshToken);

            TokenInfo tokenInfo = tokenProvider.generateToken(authentication);
            String refreshToken = tokenInfo.getRefreshToken();

            TokenUtil.updateCookie(response, refreshToken, (tokenProperty.getREFRESH_EXPIRE() / 1000) - 1); // 쿠키 만료 시간, 리프레시 토큰의 만료 시간 보다 1분 적게 설정한다.
            TokenUtil.addHeader(response, tokenInfo.getAccessToken());

            return ResponseEntity.ok().body(new AccessKakaoResDto("LOGIN", null));
        }
    }
}