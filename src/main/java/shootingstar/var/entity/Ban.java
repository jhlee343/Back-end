package shootingstar.var.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
public class Ban {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long banId;

    private String banUUID;

    @NotBlank
    private String kakaoId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User userId;

    @Builder
    public Ban(User userId, String kakaoId) {
        this.banUUID = UUID.randomUUID().toString();
        this.userId = userId;
        this.kakaoId = kakaoId;
    }
}