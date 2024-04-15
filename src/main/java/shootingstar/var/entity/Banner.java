package shootingstar.var.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Banner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bannerId;

    private String bannerUUID;

    @NotBlank
    private String bannerImgUrl;

    @NotBlank
    private String targetUrl;

    @Builder
    public Banner(String bannerImgUrl, String targetUrl) {
        this.bannerUUID = UUID.randomUUID().toString();
        this.bannerImgUrl = bannerImgUrl;
        this.targetUrl = targetUrl;
    }

    public void changeTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }
}
