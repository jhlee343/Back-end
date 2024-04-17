package shootingstar.var.jwt;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // Null 값인 필드 제외
public class TokenInfo {
    private String accessToken;
    private String refreshToken;
}
