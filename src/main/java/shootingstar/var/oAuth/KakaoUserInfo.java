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
        // Map에서 "name" 키에 해당하는 값을 먼저 검사합니다.
        Object name = kakaoAccountAttributes.get("name");

        // name 객체가 null이 아니고, 해당 문자열이 비어있지 않은지 확인합니다.
        if (name != null && !name.toString().isEmpty()) {
            return name.toString();
        }
        return null; // "name" 키가 없거나 값이 비어있는 경우 null을 반환합니다.
    }

    public String getPhoneNumber() {
        Object phoneNumber = kakaoAccountAttributes.get("phone_number");
        if (phoneNumber != null && !phoneNumber.toString().isEmpty()) {
            return phoneNumber.toString();
        }
        return null;
    }
}
