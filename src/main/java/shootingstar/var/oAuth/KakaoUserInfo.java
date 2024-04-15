package shootingstar.var.oAuth;

import lombok.Data;

import java.util.Map;

@Data
public class KakaoUserInfo {
    private Map<String, Object> attributes;
    private Map<String, Object> kakaoAccountAttributes;

    public KakaoUserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
        this.kakaoAccountAttributes = (Map<String, Object>) attributes.get("kakao_account");
    }

    public String getProviderId() {
        return attributes.get("id").toString();
    }

    public String getProfileImgUrl() {
        Map<String, Object> profile = (Map<String, Object>) kakaoAccountAttributes.get("profile");
        return profile.get("profile_image_url").toString();
    }

    public String getEmail() {
        return kakaoAccountAttributes.get("email").toString();
    }

    public String getName() {
        if (kakaoAccountAttributes.get("name").toString().isEmpty()) {
            return null;
        }
        return kakaoAccountAttributes.get("name").toString();
    }
    public String getPhoneNumber() {
        if (kakaoAccountAttributes.get("phone_number").toString().isEmpty()) {
            return null;
        }
        return kakaoAccountAttributes.get("phone_number").toString();
    }
}
