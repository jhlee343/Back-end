package shootingstar.var.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import shootingstar.var.exception.CustomException;
import shootingstar.var.exception.ErrorCode;
import shootingstar.var.jwt.JwtTokenProvider;
import shootingstar.var.repository.UserRepository;
import shootingstar.var.util.JwtRedisUtil;

import java.time.Instant;

import static shootingstar.var.exception.ErrorCode.INVALID_REFRESH_TOKEN;

@Service
@RequiredArgsConstructor
public class UserAuthService {

    private final UserRepository userRepository;
    private final JwtTokenProvider tokenProvider;

    public String refreshAccessToken(String refreshToken) {
        if (refreshToken == null) {
            throw new CustomException(INVALID_REFRESH_TOKEN);
        }
        tokenProvider.validateRefreshToken(refreshToken);
        tokenProvider.checkRefreshTokenState(refreshToken);
        Authentication authentication = tokenProvider.getAuthenticationFromRefreshToken(refreshToken);

        return tokenProvider.generateAccessToken(authentication, Instant.now());
    }
}
