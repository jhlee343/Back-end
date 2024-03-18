package shootingstar.var.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import shootingstar.var.exception.CustomException;
import shootingstar.var.exception.ErrorCode;
import shootingstar.var.jwt.JwtTokenProvider;
import shootingstar.var.repository.UserRepository;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class UserAuthService {

    private final UserRepository userRepository;
    private final JwtTokenProvider tokenProvider;

    public String refreshAccessToken(String refreshToken) {
        if (refreshToken == null) {
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }
        tokenProvider.validateRefreshToken(refreshToken);
        Authentication authentication = tokenProvider.getAuthenticationFromRefreshToken(refreshToken);

        return tokenProvider.generateAccessToken(authentication, Instant.now());
    }
}
