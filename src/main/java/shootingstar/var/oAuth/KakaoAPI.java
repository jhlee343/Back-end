package shootingstar.var.oAuth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import shootingstar.var.exception.CustomException;

import java.util.*;

import static shootingstar.var.exception.ErrorCode.*;

@Service
public class KakaoAPI {

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String clientId;
    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String redirectURI;
    @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
    private String clientSecret;
    @Value("${admin-key}")
    private String adminKey;

    private final RestTemplate restTemplate;

    public KakaoAPI(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public String getAccessTokenFromKakao(String code) {
        String tokenEndpoint = "https://kauth.kakao.com/oauth/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId); // 카카오 앱의 REST API 키
        params.add("redirect_uri", redirectURI); // 앱 설정에 등록한 리다이렉트 URI
        params.add("client_secret", clientSecret);
        params.add("code", code);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);

        try {
            ResponseEntity<Map> responseEntity = restTemplate.postForEntity(tokenEndpoint, requestEntity, Map.class);
            Map<String, Object> responseBody = responseEntity.getBody();
            if (responseBody != null && responseBody.containsKey("access_token")) {
                return responseBody.get("access_token").toString();
            } else {
                throw new CustomException(KAKAO_AUTHENTICATION_ERROR);
            }
        } catch (RestClientException e) {
            throw new CustomException(KAKAO_CONNECT_FAILED_TOKEN_ENDPOINT);
        }
    }

    public KakaoUserInfo getUserInfoFromKakao(String accessToken) {
        String userInfoEndpoint = "https://kapi.kakao.com/v2/user/me";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<?> requestEntity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map> responseEntity = restTemplate.exchange(userInfoEndpoint, HttpMethod.GET, requestEntity, Map.class);
            Map<String, Object> responseBody = responseEntity.getBody();
            if (responseBody != null) {
                return new KakaoUserInfo(responseBody);
            } else {
                throw new CustomException(KAKAO_FAILED_GET_USERINFO_ERROR);
            }
        } catch (RestClientException e) {
            throw new CustomException(KAKAO_CONNECT_FAILED_USERINFO_ENDPOINT);
        }
    }

    public void unlinkUser(String kakaoId) {
        String unlinkUserEndpoint = "https://kapi.kakao.com/v1/user/unlink";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add("Authorization", "KakaoAK " + adminKey); // 어드민 키 사용

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("target_id_type", "user_id");
        params.add("target_id", String.valueOf(kakaoId));

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);

        try {
            restTemplate.exchange(unlinkUserEndpoint, HttpMethod.POST, requestEntity, String.class);
        } catch (RestClientException e) {
            throw new CustomException(KAKAO_CONNECT_FAILED_UNLINK_ENDPOINT);
        }
    }
}
