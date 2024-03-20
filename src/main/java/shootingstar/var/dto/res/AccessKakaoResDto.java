package shootingstar.var.dto.res;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL) // Null 값인 필드 제외
public class AccessKakaoResDto {
    private String type;
    private KakaoUserResDto kakaoUserResDto;

    public AccessKakaoResDto(String type, KakaoUserResDto kakaoUserResDto) {
        this.type = type;
        this.kakaoUserResDto = kakaoUserResDto;
    }
}
