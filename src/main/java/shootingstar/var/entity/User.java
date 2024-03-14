package shootingstar.var.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @NotBlank
    private String kakaoId;

    @NotBlank
    private String name;

    @NotBlank
    private String nickname;

    @NotBlank
    private String phone;

    @NotBlank
    @Email
    private String email;

    private String profileImgUrl;

    @Enumerated(EnumType.STRING)
    private UserType userType;

    @NotBlank
    private Long point;

    @NotBlank
    private Long donationPrice;

    private Double rating;

    private LocalDateTime subscribe;

    private Integer warningCount;

    public User(String kakaoId, String name, String nickname, String phone, String email, String profileImgUrl, UserType userType) {
        this.kakaoId = kakaoId;
        this.name = name;
        this.nickname = nickname;
        this.phone = phone;
        this.email = email;
        this.profileImgUrl = profileImgUrl;
        this.userType = userType;
        this.point = 0L;
        this.donationPrice = 0L;
        this.rating = null;
        this.subscribe = null;
        this.warningCount = 0;
    }
}