package shootingstar.var.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.StringUtils;

public class TokenUtil {

    private static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";
    private static final String ACCESS_TOKEN_HEADER_NAME = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private TokenUtil() {}

    // 쿠키에서 리프레시 토큰을 추출하는 메서드
    public static String getTokenFromCookie(HttpServletRequest request) {
        String refreshToken = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refreshToken".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }
        return refreshToken;
    }

    // 헤더에서 엑세스 토큰을 추출하는 메서드
    public static String getTokenFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization"); // 헤더에 존재하는 엑세스 토큰을 받아온다.

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")) {
            return bearerToken.substring(7);
        }
        return bearerToken;
    }

    public static void addHeader(HttpServletResponse response, String accessToken) {
        response.setHeader(ACCESS_TOKEN_HEADER_NAME, BEARER_PREFIX + accessToken);
    }

    public static void updateCookie(HttpServletResponse response, String value, int age) {
        // 쿠키에 존재하는 리프레시 토큰을 삭제한다.
        Cookie cookie = new Cookie("refreshToken", value);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setAttribute("SameSite", "None");
        cookie.setPath("/");
        cookie.setMaxAge(age);

        response.addCookie(cookie);
    }
}
