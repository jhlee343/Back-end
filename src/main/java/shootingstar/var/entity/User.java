package shootingstar.var.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String userUUID;

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

    @NotBlank
    private String profileImgUrl;

    @NotNull
    @Enumerated(EnumType.STRING)
    private UserType userType;

    private Long point;

    private Long donationPrice;

    private Double rating;

    private LocalDateTime subscribe;

    private Integer warningCount;

    private Boolean isWithdrawn;
    private LocalDateTime withdrawnTime;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private final List<Auction> myHostedAuction = new ArrayList<>();

    @OneToMany(mappedBy = "organizer", fetch = FetchType.LAZY)
    private final List<Ticket> myHostedTicket = new ArrayList<>();

    @OneToMany(mappedBy = "winner", fetch = FetchType.LAZY)
    private final List<Ticket> winningTicket = new ArrayList<>();

    @Builder
    public User(String kakaoId, String name, String nickname, String phone, String email, String profileImgUrl, UserType userType) {
        this.userUUID = UUID.randomUUID().toString();
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
        this.isWithdrawn = false;
        this.withdrawnTime = null;
    }

    public void increasePoint(long point) {
        this.point += point;
    }

    public void decreasePoint(long point) {
        this.point -= point;
    }

    public void withdrawn() {
        this.isWithdrawn = true;
        this.withdrawnTime = LocalDateTime.now();
    }
}