package shootingstar.var.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import shootingstar.var.Service.UserAuthService;
import shootingstar.var.dto.req.AccessKakaoReqDto;
import shootingstar.var.dto.res.AccessKakaoResDto;
import shootingstar.var.dto.res.KakaoUserResDto;
import shootingstar.var.exception.ErrorResponse;
import shootingstar.var.jwt.JwtTokenProvider;
import shootingstar.var.jwt.TokenInfo;
import shootingstar.var.jwt.TokenProperty;
import shootingstar.var.oAuth.KakaoAPI;
import shootingstar.var.oAuth.KakaoUserInfo;
import shootingstar.var.util.TokenUtil;

@Tag(name = "인증 컨트롤러", description = "사용자 인증 관련 컨트롤러")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class UserAuthController {

    private final UserAuthService authService;
    private final KakaoAPI kakaoAPI;
    private final JwtTokenProvider tokenProvider;
    private final TokenProperty tokenProperty;

    @Operation(summary = "카카오 서버 연결 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description =
                                    "- 카카오와 정상적으로 연결되었을 때\n" +
                                    "   - 가입되지 않은 회원은 type : JOIN 과 함께 카카오 정보를 반환\n" +
                                    "   - 가입된 회원인 경우 type : LOGIN 과 함께 토큰을 반환",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = AccessKakaoResDto.class))}),
            @ApiResponse(responseCode = "401",
                    description =
                                    "- 카카오로부터 ACCESS 토큰 획득에 실패 : 0110\n" +
                                    "- 카카오 토큰 엔드포인트와 통신에 실패 : 0111\n" +
                                    "- 카카오로부터 사용자 정보를 가져오지 못했을 때 : 0112\n" +
                                    "- 카카오 사용자 정보 엔드포인트와 통신에 실패 : 0113",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @PostMapping("/oauth2/accessKakao")
    public ResponseEntity<AccessKakaoResDto> accessKakao(@RequestBody AccessKakaoReqDto reqDto, HttpServletRequest request, HttpServletResponse response) {
        String accessTokenFromKakao = kakaoAPI.getAccessTokenFromKakao(reqDto.getCode());
        KakaoUserInfo kakaoUserInfo = kakaoAPI.getUserInfoFromKakao(accessTokenFromKakao);

        Authentication authentication = authService.loadUserByKakaoId(kakaoUserInfo.getProviderId());

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

    @DeleteMapping("/logout")
    public ResponseEntity<String> logout() {
        return ResponseEntity.ok().body("로그아웃");
    }

    @Operation(summary = "액세스 토큰 재발급 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "액세스 토큰 재발급 성공", content = {
                    @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))}),
            @ApiResponse(responseCode = "403",
                    description =
                                    "- 잘못된 RefreshToken : 0106\n" +
                                    "- 만료된 RefreshToken : 0107\n" +
                                    "- 지원하지 않는 RefreshToken : 0108\n" +
                                    "- Claim이 빈 Refresh Token : 0109",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @PostMapping("/refresh")
    public ResponseEntity<String> refresh(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = TokenUtil.getTokenFromCookie(request);

        String newAccessToken = authService.refreshAccessToken(refreshToken);

        TokenUtil.addHeader(response, newAccessToken);

        return ResponseEntity.ok().body("성공적으로 액세스 토큰이 재발행 되었습니다.");
    }
}
