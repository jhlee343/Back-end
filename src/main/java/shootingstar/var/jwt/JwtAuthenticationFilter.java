package shootingstar.var.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;
import shootingstar.var.exception.CustomException;
import shootingstar.var.exception.ErrorCode;
import shootingstar.var.exception.ErrorResponse;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {

    private final JwtTokenProvider jwtTokenProvider;

    // JWT 인증 필터
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String token = resolveToken(httpRequest);
        String requestURI = httpRequest.getRequestURI();
        log.info("JwtAuthenticationFilter requestURI :  {}, addr : {}", requestURI, httpRequest.getRemoteAddr());
        /*
          /api/auth/refresh, /api/auth/logout, /error 엔드 포인트는 JWT 검증을 하지 않는다
          /api/auth/refresh, /api/auth/logout 엔드 포인트는 Controller, Service 에서 별도의 검증을 한다.
         */
        if (requestURI.equals("/api/auth/refresh") || requestURI.equals("/api/auth/logout") || requestURI.equals("/error")) {
            chain.doFilter(request, response);
            return;
        }

        // 엑세스 토큰 검증 및 인증 정보 설정
        /*
          검증을 하지 않는 엔드 포인트
          - 토큰이 null 일 때 다음 체인으로 연결된다.
          - 검증을 하지 않는 엔드 포인트에 토큰을 사용했을 경우 토큰 검을을 한다.
            이 때 만약 잘못된 토큰 값이라면 토큰 에러를 반환한다.
          - 존재하지 않는 엔드 포인트에 접근을 하게 되면 토큰이 null 일 때 다음 체인으로 연결되고 잘못된 엔드 포인트라는 에러를 반환한다.
            토큰을 가지고 있을 경우에는 토큰 검증을 한 뒤 다음 체인으로 연결되고 잘못된 엔드 포인트라는 에러를 반환한다.
            이 때 만약 잘못된 토큰 값이라면 토큰 에러를 반환한다.
         */
        /*
          검증이 필요한 엔드 포인트의 경우
          - 토큰이 null 일 경우 인증에 실패하게 된다.
          - 존재하지 않는 엔드 포인트에 접근을 하게 되면 토큰을 통해 인증을 한 뒤 엔드 포인트라는 에러를 반환한다.
            이 때 만약 잘못된 토큰 값이라면 토큰 에러를 반환한다.
         */
        try {
            if (token != null && jwtTokenProvider.validateToken(token)) {
                Authentication authentication = jwtTokenProvider.getAuthenticationFromAccessToken(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (CustomException e) {
            // 검증 과정에서 예외가 발생했을 경우
            extracted((HttpServletResponse) response, e);
            return;
        }

        chain.doFilter(request, response);
    }

    private void extracted(HttpServletResponse response, CustomException e) throws IOException {
        ErrorCode errorCode = e.getErrorCode();
        HttpStatus httpStatus = errorCode.getHttpStatus();
        ErrorResponse errorResponse = new ErrorResponse(errorCode);

        response.setStatus(httpStatus.value()); // HTTP 상태 코드 설정
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");

        ObjectMapper objectMapper = new ObjectMapper();
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse)); // JSON 형태로 에러 응답 작성
    }

    // Request Header 에서 토큰 정보 추출
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
