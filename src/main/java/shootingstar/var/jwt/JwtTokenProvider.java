package shootingstar.var.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import shootingstar.var.exception.CustomException;
import shootingstar.var.exception.ErrorCode;
import shootingstar.var.oAuth.OAuth2UserService;

import java.security.Key;
import java.util.*;

@Component
@Slf4j
public class JwtTokenProvider {
    private final Key accessKey;
    private final Key refreshKey;
    private final TokenProperty tokenProperty;
    private final OAuth2UserService oAuth2UserService;

    public JwtTokenProvider(@Value("${jwt.secret-access}") String accessSecretKey,
                            @Value("${jwt.secret-refresh}") String refreshSecretKey,
                            TokenProperty tokenProperty, OAuth2UserService oAuth2UserService) {
        this.tokenProperty = tokenProperty;
        this.oAuth2UserService = oAuth2UserService;
        byte[] accessKeyBytes = Decoders.BASE64.decode(accessSecretKey);
        byte[] refreshKeyBytes = Decoders.BASE64.decode(refreshSecretKey);

        this.accessKey = Keys.hmacShaKeyFor(accessKeyBytes);
        this.refreshKey = Keys.hmacShaKeyFor(refreshKeyBytes);
    }

    // 유저 정보를 가지고 AccessToken, RefreshToken 을 생성하는 메서드
    public TokenInfo generateToken(Authentication authentication) {
        long now = (new Date()).getTime();
        String accessToken = generateAccessToken(authentication, now);
        String refreshToken = generateRefreshToken(authentication, now);

        return new TokenInfo("Bearer", accessToken, refreshToken, (new Date(now + tokenProperty.getREFRESH_EXPIRE()).getTime()));
    }

    // 권한 가지고 오기
    private String getAuthoritiesString(Collection<? extends GrantedAuthority> authorities) {
        return authorities.stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElseThrow(() -> new CustomException(ErrorCode.ACCESS_DENIED));
    }


    // 엑세스 토큰 생성 메서드
    public String generateAccessToken(Authentication authentication, long now) {
        Date accessTokenExpiresIn = new Date(now + tokenProperty.getACCESS_EXPIRE()); // 30분 후 만료
        return Jwts.builder()
                .setSubject(authentication.getName())
                .claim("auth", getAuthoritiesString(authentication.getAuthorities()))
                .setIssuedAt(new Date(now))
                .setExpiration(accessTokenExpiresIn)
                .signWith(accessKey, SignatureAlgorithm.HS256)
                .compact();
    }

    // 리프레시 토큰 생성 메서드
    public String generateRefreshToken(Authentication authentication, long now) {
        Date refreshTokenExpiresIn = new Date(now + tokenProperty.getREFRESH_EXPIRE()); // 1일 후 만료

        return Jwts.builder()
                .setSubject(authentication.getName())
                .setIssuedAt(new Date(now))
                .setExpiration(refreshTokenExpiresIn)
                .signWith(refreshKey, SignatureAlgorithm.HS256)
                .compact();
    }

    // 키를 통해 토큰을 복화화 한다.
    private Claims parseClaims(String token, Key key) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    // access 토큰을 복호화하여 토큰에 들어있는 정보를 꺼내는 메서드
    public Authentication getAuthenticationFromAccessToken(String accessToken) {
        // 토큰 복호화
        Claims claims = parseClaims(accessToken, accessKey);

        if (claims.get("auth") == null) {
            log.info("권한 정보가 없는 토큰입니다.");
            throw new CustomException(ErrorCode.ILLEGAL_ACCESS_TOKEN);
        }

        String subject = claims.getSubject();

        GrantedAuthority authority = new SimpleGrantedAuthority(claims.get("auth").toString());
        List<GrantedAuthority> authorities = Collections.singletonList(authority);

        return new UsernamePasswordAuthenticationToken(subject, null, authorities);
    }

    // refresh 토큰을 복호화하여 토큰에 들어있는 정보를 꺼내는 메서드
    public Authentication getAuthenticationFromRefreshToken(String token) {
        Claims claims = parseClaims(token, refreshKey);

        if (claims.getSubject() == null) {
            log.info("토큰에서 사용자 식별 정보를 찾을 수 없습니다.");
            throw new CustomException(ErrorCode.ILLEGAL_REFRESH_TOKEN);
        }

        String subject = claims.getSubject();

        String authority = oAuth2UserService.getUserAuthorityById(subject);
        List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList(authority);

        return new UsernamePasswordAuthenticationToken(subject, null, authorities);
    }

    // access 토큰 정보를 검증하는 메서드
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(accessKey).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("Invalid ACCESS token");
            throw new CustomException(ErrorCode.INVALID_ACCESS_TOKEN);
        } catch (ExpiredJwtException e) {
            log.info("Expired ACCESS token");
            throw new CustomException(ErrorCode.EXPIRED_ACCESS_TOKEN);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported ACCESS token");
            throw new CustomException(ErrorCode.UNSUPPORTED_ACCESS_TOKEN);
        } catch (IllegalArgumentException e) {
            log.info("ACCESS claims string is empty.");
            throw new CustomException(ErrorCode.ILLEGAL_ACCESS_TOKEN);
        }
    }

    // refresh 토큰 정보를 검증하는 메서드
    public void validateRefreshToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(refreshKey).build().parseClaimsJws(token);
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("Invalid REFRESH token");
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        } catch (ExpiredJwtException e) {
            log.info("Expired REFRESH token");
            throw new CustomException(ErrorCode.EXPIRED_REFRESH_TOKEN);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported REFRESH token");
            throw new CustomException(ErrorCode.UNSUPPORTED_REFRESH_TOKEN);
        } catch (IllegalArgumentException e) {
            log.info("REFRESH claims string is empty.");
            throw new CustomException(ErrorCode.ILLEGAL_REFRESH_TOKEN);
        }
    }
}