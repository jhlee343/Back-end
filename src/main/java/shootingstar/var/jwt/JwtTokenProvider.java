package shootingstar.var.jwt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import shootingstar.var.entity.User;
import shootingstar.var.exception.CustomException;
import shootingstar.var.exception.ErrorCode;
import shootingstar.var.repository.UserRepository;
import shootingstar.var.util.JwtRedisUtil;
import shootingstar.var.util.LoginListRedisUtil;
import shootingstar.var.util.TokenUtil;

import java.security.Key;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static shootingstar.var.exception.ErrorCode.*;

@Component
@Slf4j
public class JwtTokenProvider {
    private final Key accessKey;
    private final Key refreshKey;
    private final TokenProperty tokenProperty;
    private final JwtRedisUtil jwtRedisUtil;
    private final LoginListRedisUtil loginListRedisUtil;
    private final UserRepository userRepository;

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    public JwtTokenProvider(@Value("${jwt.secret-access}") String accessSecretKey,
                            @Value("${jwt.secret-refresh}") String refreshSecretKey,
                            TokenProperty tokenProperty, UserRepository userRepository, JwtRedisUtil jwtRedisUtil, LoginListRedisUtil loginListRedisUtil) {
        this.tokenProperty = tokenProperty;
        this.jwtRedisUtil = jwtRedisUtil;
        this.loginListRedisUtil = loginListRedisUtil;
        this.userRepository = userRepository;
        byte[] accessKeyBytes = Decoders.BASE64.decode(accessSecretKey);
        byte[] refreshKeyBytes = Decoders.BASE64.decode(refreshSecretKey);

        this.accessKey = Keys.hmacShaKeyFor(accessKeyBytes);
        this.refreshKey = Keys.hmacShaKeyFor(refreshKeyBytes);
    }

    // 유저 정보를 가지고 AccessToken, RefreshToken 을 생성하는 메서드
    public TokenInfo generateToken(Authentication authentication) {
        Instant now = Instant.now();
        String accessToken = generateAccessToken(authentication, now);

        Instant refreshTokenExpiresIn = now.plus(tokenProperty.getREFRESH_EXPIRE(), ChronoUnit.MILLIS);
        String refreshToken = generateRefreshToken(authentication, now, refreshTokenExpiresIn);

        return new TokenInfo(accessToken, refreshToken);
    }

    // 권한 가지고 오기
    private String getAuthoritiesString(Collection<? extends GrantedAuthority> authorities) {
        return authorities.stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElseThrow(() -> new CustomException(ACCESS_DENIED));
    }


    // 엑세스 토큰 생성 메서드
    public String generateAccessToken(Authentication authentication, Instant now) {
        Instant accessTokenExpiresIn = now.plus(tokenProperty.getACCESS_EXPIRE(), ChronoUnit.MILLIS); // 30분 후 만료

        return Jwts.builder()
                .setSubject(authentication.getName())
                .claim("auth", getAuthoritiesString(authentication.getAuthorities()))
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(accessTokenExpiresIn))
                .signWith(accessKey, SignatureAlgorithm.HS256)
                .compact();
    }

    // 리프레시 토큰 생성 메서드
//    public String generateRefreshToken(Authentication authentication, Instant now, Instant refreshTokenExpiresIn) {
//        String refreshToken = Jwts.builder()
//                .setSubject(authentication.getName())
//                .setIssuedAt(Date.from(now))
//                .setExpiration(Date.from(refreshTokenExpiresIn))
//                .signWith(refreshKey, SignatureAlgorithm.HS256)
//                .compact();
//
//        saveRefreshTokenAtRedis(refreshToken, refreshTokenExpiresIn);
//
//        return refreshToken;
//    }

    public String generateRefreshToken(Authentication authentication, Instant now, Instant refreshTokenExpiresIn) {
        String refreshToken = Jwts.builder()
                .setSubject(authentication.getName())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(refreshTokenExpiresIn))
                .signWith(refreshKey, SignatureAlgorithm.HS256)
                .compact();

        String beforeRefreshToken = loginListRedisUtil.getData(authentication.getName());

        if (beforeRefreshToken != null) {
            expiredRefreshTokenAtRedis(beforeRefreshToken);
        }

        saveRefreshTokenAtRedis(refreshToken, refreshTokenExpiresIn);
        saveLoginListWithRefreshTokenAtRedis(authentication.getName(), refreshToken, refreshTokenExpiresIn);

        return refreshToken;
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
            throw new CustomException(ILLEGAL_ACCESS_TOKEN);
        }

        String subject = claims.getSubject();

        GrantedAuthority authority = new SimpleGrantedAuthority(claims.get("auth").toString());
        List<GrantedAuthority> authorities = Collections.singletonList(authority);

        return new UsernamePasswordAuthenticationToken(subject, null, authorities);
    }

    // refresh 토큰을 복호화하여 토큰에 들어있는 정보를 꺼내는 메서드
    public Authentication getAuthenticationFromRefreshToken(String refreshToken) {
        Claims claims = parseClaims(refreshToken, refreshKey);

        if (claims.getSubject() == null) {
            log.info("토큰에서 사용자 식별 정보를 찾을 수 없습니다.");
            throw new CustomException(ILLEGAL_REFRESH_TOKEN);
        }

        String subject = claims.getSubject();

        Optional<User> optionalUser = userRepository.findByUserUUID(subject);
        if (optionalUser.isEmpty()) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }
        String authority = optionalUser.get().getUserType().toString();

        List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList(authority);

        return new UsernamePasswordAuthenticationToken(subject, null, authorities);
    }

    // access 토큰 정보를 검증하는 메서드
    public boolean validateAccessToken(String token) {
        if (token == null) {
            log.info("Not Found ACCESS token");
            throw new CustomException(INVALID_ACCESS_TOKEN);
        }

        try {
            Jwts.parserBuilder().setSigningKey(accessKey).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("Invalid ACCESS token");
            throw new CustomException(INVALID_ACCESS_TOKEN);
        } catch (ExpiredJwtException e) {
            log.info("Expired ACCESS token");
            throw new CustomException(EXPIRED_ACCESS_TOKEN);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported ACCESS token");
            throw new CustomException(UNSUPPORTED_ACCESS_TOKEN);
        } catch (IllegalArgumentException e) {
            log.info("ACCESS claims string is empty.");
            throw new CustomException(ILLEGAL_ACCESS_TOKEN);
        }
    }

    // refresh 토큰 정보를 검증하는 메서드
    public void validateRefreshToken(String token) {
        if (token == null) {
            log.info("Not Found REFRESH token");
            throw new CustomException(INVALID_REFRESH_TOKEN);
        }

        try {
            Jwts.parserBuilder().setSigningKey(refreshKey).build().parseClaimsJws(token);
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("Invalid REFRESH token");
            throw new CustomException(INVALID_REFRESH_TOKEN);
        } catch (ExpiredJwtException e) {
            log.info("Expired REFRESH token");
            throw new CustomException(EXPIRED_REFRESH_TOKEN);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported REFRESH token");
            throw new CustomException(UNSUPPORTED_REFRESH_TOKEN);
        } catch (IllegalArgumentException e) {
            log.info("REFRESH claims string is empty.");
            throw new CustomException(ILLEGAL_REFRESH_TOKEN);
        }
    }
    // ----------------------------------------------------------------------------------------------------------------
    /**
     * JwtRedis 리프레시 토큰 정보 저장
     */
    private void saveRefreshTokenAtRedis(String refreshToken, Instant refreshTokenExpiresIn) {
        // 리프레시 토큰의 만료 시간을 원하는 형식으로 포맷팅
        String formattedDate = dateTimeFormatter.withZone(ZoneId.systemDefault())
                .format(refreshTokenExpiresIn);

        // JWT redis 에 저장될 value 정보 생성
        String valueToStore = String.format("{\"status\":\"%s\", \"expireTime\":\"%s\"}", "active", formattedDate);

        long ttl = Duration.between(Instant.now(), refreshTokenExpiresIn).toMillis();

        // JWT redis 에 key : 리프레시 토큰, value : 상태, 만료시간 을 저장
        jwtRedisUtil.setDataExpire(refreshToken, valueToStore , ttl);
    }

    /**
     * JwtRedis 리프레시 토큰 만료 처리
     */
    public void expiredRefreshTokenAtRedis(String refreshToken) {
        String data = jwtRedisUtil.getData(refreshToken); // 해당 리프레시 토큰 무효화
        if (data == null) {
            return;
        }
        try {
            // 토큰 데이터를 JSON 형태로 변환한다,
            JsonNode tokenData = objectMapper.readTree(data);

            // 이미 만료된 토큰이라면 종료
            if (Objects.equals(tokenData.get("status").asText(), "expired")) {
                return;
            }

            // 상태 정보를 expired 로 변경한다.
            ((ObjectNode) tokenData).put("status", "expired");

            // 현재 토큰의 만료 시간을 체크한다.
            Instant expireTime = ZonedDateTime.parse(tokenData.get("expireTime").asText(), dateTimeFormatter.withZone(ZoneId.systemDefault())).toInstant();
            Instant now = Instant.now();

            // 현재를 기준으로 만료까지 남은 시간을 계산한다.
            long millisecondsLeft = Duration.between(now, expireTime).toMillis();

            // 업데이트된 정보와 계산된 만료 시간을 Redis에 저장
            if (millisecondsLeft > 0) {
                jwtRedisUtil.setDataExpire(refreshToken, tokenData.toString(), millisecondsLeft);
            } else {
                // 이미 만료된 경우, Redis에서 해당 토큰 삭제
                jwtRedisUtil.deleteData(refreshToken);
            }
        }  catch (JsonProcessingException e) {
            log.info(e.getMessage());
            throw new CustomException(SERVER_ERROR);
        }
    }

    /**
     * JwtRedis 리프레시 토큰 만료 상태 확인
     */
    public void checkRefreshTokenState(String token) {
        // jwt redis 에서 토큰의 정보를 가지고 온다.
        String data = jwtRedisUtil.getData(token);
        if (data == null) {
            throw new CustomException(INVALID_REFRESH_TOKEN);
        }
        try {
            // JSON 문자열을 JsonNode로 파싱
            JsonNode jsonNode = objectMapper.readTree(data);

            // "status" 필드의 값을 가져옴
            String status = jsonNode.get("status").asText();

            // status 값이 active 가 아닌 경우 무효화 된 토큰이다.
            if (status.equals("expired")) {
                throw new CustomException(EXPIRED_REFRESH_TOKEN);
            }
        } catch (JsonProcessingException e) {
            log.info(e.getMessage());
            throw new CustomException(SERVER_ERROR);
        }
    }

    // ----------------------------------------------------------------------------------------------------------------

    /**
     * LoginListRedis 로그인 정보 저장
     */
    private void saveLoginListWithRefreshTokenAtRedis(String userUUID, String refreshToken, Instant refreshTokenExpiresIn) {
        long ttl = Duration.between(Instant.now(), refreshTokenExpiresIn).toMillis();
        loginListRedisUtil.setDataExpire(userUUID, refreshToken , ttl);
    }

    /**
     * LoginListRedis 로그인 확인
     */
    public boolean isLoginUser(String userUUID) {
        return loginListRedisUtil.getData(userUUID) != null;
    }

    // ----------------------------------------------------------------------------------------------------------------

    public String getUserUUIDByRequest(HttpServletRequest request) {
        String accessToken = TokenUtil.getTokenFromHeader(request);
        Authentication authentication = getAuthenticationFromAccessToken(accessToken);
        return authentication.getName();
    }
}