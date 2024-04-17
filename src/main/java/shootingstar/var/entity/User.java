package shootingstar.var.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shootingstar.var.entity.auction.Auction;
import shootingstar.var.entity.ticket.Ticket;
import shootingstar.var.enums.type.UserType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import shootingstar.var.exception.CustomException;
import shootingstar.var.exception.ErrorCode;

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

    @Column(precision = 10, scale = 2)
    private BigDecimal point;

    private Long donationPrice;

    private Double rating;

    private LocalDateTime subscribeExpiration;

    private Integer warningCount;

    private Boolean isWithdrawn;
    private LocalDateTime withdrawnTime;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private final List<Auction> myHostedAuction = new ArrayList<>();

    @OneToMany(mappedBy = "organizer", fetch = FetchType.LAZY)
    private final List<Ticket> myHostedTicket = new ArrayList<>();

    @OneToMany(mappedBy = "winner", fetch = FetchType.LAZY)
    private final List<Ticket> winningTicket = new ArrayList<>();

    @OneToMany(mappedBy = "receiver")
    private final List<Review> reviewsReceived = new ArrayList<>();

    @OneToOne(mappedBy = "user")
    private VipInfo vipInfo;

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
        this.point = new BigDecimal(0);
        this.donationPrice = 0L;
        this.rating = null;
        this.subscribeExpiration = null;
        this.warningCount = 0;
        this.isWithdrawn = false;
        this.withdrawnTime = null;
    }

    public void increasePoint(BigDecimal point) {
        this.point = this.point.add(point);
    }

    public void decreasePoint(BigDecimal point) {
        this.point = this.point.subtract(point);
    }

    public void withdrawn() {
        this.isWithdrawn = true;
        this.withdrawnTime = LocalDateTime.now();
    }

    public void updateRating(double rating) {
        this.rating = rating;
    }

    public void subscribeActivate() {
        this.subscribeExpiration = LocalDateTime.now().plusDays(30L);
    }

    public void setWarningCount(int warningCount) {
        this.warningCount = warningCount;
    }

    public void validateSubscribeExpiration() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        if (this.subscribeExpiration == null || this.subscribeExpiration.isBefore(currentDateTime)) {
            throw new CustomException(ErrorCode.EXPIRED_SUBSCRIPTION);
        }
    }

    public void changeUserType(UserType userType) {
        this.userType = userType;
    }
}