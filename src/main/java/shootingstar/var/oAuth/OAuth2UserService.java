package shootingstar.var.oAuth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import shootingstar.var.entity.User;
import shootingstar.var.exception.CustomException;
import shootingstar.var.exception.ErrorCode;
import shootingstar.var.repository.UserRepository;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class OAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String id = oAuth2User.getAttributes().get("id").toString();
        Optional<User> findUser = userRepository.findByKakaoId(id);

        List<GrantedAuthority> authorities;
        if (findUser.isPresent()) { // 이미 존재하는 사용자인 경우
            Map<String, Object> attributes = new HashMap<>(oAuth2User.getAttributes());

            User user = findUser.get();
            authorities = AuthorityUtils.createAuthorityList(user.getUserType().toString());
            attributes.put("userId", user.getUserUUID().toString());

            // 사용자 UUID를 기반으로 DefaultOAuth2User 객체를 생성
            return new DefaultOAuth2User(authorities, attributes, "userId");
        }
        else { // 새로운 사용자인 경우
            authorities = AuthorityUtils.createAuthorityList("ROLE_GUEST");
            String userNameAttributeName = userRequest.getClientRegistration()
                    .getProviderDetails()
                    .getUserInfoEndpoint()
                    .getUserNameAttributeName();

            // 카카오 ID를 기반으로 DefaultOAuth2User 객체를 생성
            return new DefaultOAuth2User(authorities, oAuth2User.getAttributes(), userNameAttributeName);
        }
    }

    public String getUserAuthorityUUID(String userUUID) {
        Optional<User> optionalUser = userRepository.findByUserUUID(UUID.fromString(userUUID));
        if (optionalUser.isEmpty()) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }
        return optionalUser.get().getUserType().toString();
    }
}
