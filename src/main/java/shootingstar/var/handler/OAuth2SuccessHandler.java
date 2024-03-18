package shootingstar.var.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import shootingstar.var.exception.CustomException;
import shootingstar.var.exception.ErrorCode;
import shootingstar.var.oAuth.KakaoUserInfo;
import shootingstar.var.oAuth.KakaoUserResDto;
import shootingstar.var.jwt.JwtTokenProvider;
import shootingstar.var.jwt.TokenInfo;
import shootingstar.var.jwt.TokenProperty;
import shootingstar.var.util.TokenUtil;

import java.util.Collection;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler {

    private final JwtTokenProvider tokenProvider;
    private final TokenProperty tokenProperty;

    @Bean
    public AuthenticationSuccessHandler successHandler() {
        ObjectMapper objectMapper = new ObjectMapper();

        return (HttpServletRequest request, HttpServletResponse response, Authentication authentication) -> {
            DefaultOAuth2User defaultOAuth2User = (DefaultOAuth2User) authentication.getPrincipal();

            Collection<? extends GrantedAuthority> authorities = defaultOAuth2User.getAuthorities();

            // Optional 객체가 비어있지 않다면, 권한의 이름을 문자열로 가져옵니다.
            String authorityName = extractFirstAuthority(authorities);

            if (authorityName.equals("ROLE_GUEST")) {
                KakaoUserInfo kakaoUserInfo = new KakaoUserInfo(defaultOAuth2User.getAttributes());
                response.setContentType("application/json;charset=UTF-8");
                response.setStatus(HttpStatus.OK.value());
                objectMapper.writeValue(response.getWriter(), buildUserJson(kakaoUserInfo));
            } else {
                String oldRefreshToken = TokenUtil.getTokenFromCookie(request);
                if (oldRefreshToken != null) tokenProvider.expiredRefreshToken(oldRefreshToken);
                TokenInfo tokenInfo = tokenProvider.generateToken(authentication);
                String refreshToken = tokenInfo.getRefreshToken();
                TokenUtil.updateCookie(response, refreshToken, (tokenProperty.getREFRESH_EXPIRE() / 1000) - 1); // 쿠키 만료 시간, 리프레시 토큰의 만료 시간 보다 1분 적게 설정한다.
                response.setHeader("Authorization", "Bearer " + tokenInfo.getAccessToken());
            }
        };
    }

    private String extractFirstAuthority(Collection<? extends GrantedAuthority> authorities) {
        return authorities.stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElseThrow(() -> new CustomException(ErrorCode.ACCESS_DENIED));
    }

    private KakaoUserResDto buildUserJson(KakaoUserInfo kakaoUserInfo) {
        return new KakaoUserResDto(
                kakaoUserInfo.getProviderId(),
                kakaoUserInfo.getName(),
                kakaoUserInfo.getEmail(),
                kakaoUserInfo.getPhoneNumber(),
                kakaoUserInfo.getProfileImgUrl());
    }
}
