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
import shootingstar.var.util.LoginListRedisUtil;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static shootingstar.var.exception.ErrorCode.INVALID_REFRESH_TOKEN;
import static shootingstar.var.exception.ErrorCode.LOGGED_IN_SOMEWHERE_ELSE;

@Service
@RequiredArgsConstructor
public class UserAuthService {

    private final UserRepository userRepository;
    private final JwtTokenProvider tokenProvider;
    private final LoginListRedisUtil loginListRedisUtil;

    public String refreshAccessToken(String refreshToken) {
        tokenProvider.validateRefreshToken(refreshToken); // 토큰 위변조 체크
        Authentication authentication = tokenProvider.getAuthenticationFromRefreshToken(refreshToken);

        String userUUID = authentication.getName();
        String storeRefreshToken = loginListRedisUtil.getData(userUUID);
        // 현재 로그인 리스트에 등록된 토큰과 다를 경우
        // 현재 로그인 리스트에 등록되진 않았지만 리프레시 토큰을 들고 있는 경우는 다른 곳에서 로그인한 뒤 그곳에서 로그아웃을 한 경우이다.
        // 위 두 경우 모두 다른 곳에서 로그인 되었다는 뜻이기에 에러를 반환
        if (!Objects.equals(storeRefreshToken, refreshToken)) {
            throw new CustomException(LOGGED_IN_SOMEWHERE_ELSE);
        }

        tokenProvider.checkRefreshTokenState(refreshToken);

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

    public void logout(String refreshToken) {
        tokenProvider.validateRefreshToken(refreshToken);

        Authentication authentication = tokenProvider.getAuthenticationFromRefreshToken(refreshToken);

        String userUUID = authentication.getName();

        tokenProvider.expiredRefreshTokenAtRedis(refreshToken); // 현재 토큰을 만료 처리한다

        String storeRefreshToken = loginListRedisUtil.getData(userUUID); // 로그인 리스트에 등록된 토큰
        if (!Objects.equals(storeRefreshToken, refreshToken)) { // 로그인 리스트와 현재 등록된 토큰이 다를 경우
            tokenProvider.expiredRefreshTokenAtRedis(storeRefreshToken); // 로그인 리스트에 등록된 토큰도 만료 시킨다.
        }
        loginListRedisUtil.deleteData(userUUID); // 로그인 리스트에서 사용자 정보를 제거한다.
    }
}
