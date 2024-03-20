package shootingstar.var.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Service;
import shootingstar.var.entity.User;
import shootingstar.var.exception.CustomException;
import shootingstar.var.jwt.JwtTokenProvider;
import shootingstar.var.repository.UserRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

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

    public Authentication loadUserByKakaoId(String kakaoId) {
        // 카카오 ID로 사용자 조회
        Optional<User> findUser = userRepository.findByKakaoId(kakaoId);

        // 이미 존재하는 사용자인 경우
        if (findUser.isPresent()) {
            User user = findUser.get();
            List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList(user.getUserType().toString());

            // 사용자 UUID를 기반으로 Authentication 객체 생성 및 반환
            return new UsernamePasswordAuthenticationToken(user.getUserUUID(), null, authorities);
        }

        // 존재하지 않는 사용자인 경우 다른 로직을 처리할 수 있도록 null 반환
        return null;
    }
}
