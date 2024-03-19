package shootingstar.var.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shootingstar.var.annotation.After30Days;

@Entity
@Getter
@NoArgsConstructor
public class Auction extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long auctionId;

    @Column(columnDefinition = "BINARY(16)")
    private UUID auctionUUID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @NotNull
    private User user;

    @Min(value = 100000)
    private long minBidAmount;

    @After30Days
    private LocalDateTime meetingDate;

    @NotBlank
    private String meetingLocation;

    @Lob
    @NotBlank
    private String meetingInfoText;

    @Lob
    @NotBlank
    private String meetingPromiseText;

    private String meetingInfoImg;

    private String meetingPromiseImg;

    private long currentHighestBidAmount;

    @Column(columnDefinition = "BINARY(16)")
    private UUID currentHighestBidderId;

    private long bidCount;

    @NotNull
    @Enumerated(value = EnumType.STRING)
    private AuctionType auctionType;

    @Builder
    public Auction(User user, long minBidAmount, LocalDateTime meetingDate, String meetingLocation,
                   String meetingInfoText, String meetingPromiseText, String meetingInfoImg, String meetingPromiseImg) {
        this.auctionUUID = UUID.randomUUID();
        this.user = user;
        this.minBidAmount = minBidAmount;
        this.meetingDate = meetingDate;
        this.meetingLocation = meetingLocation;
        this.meetingInfoText = meetingInfoText;
        this.meetingPromiseText = meetingPromiseText;
        this.meetingInfoImg = meetingInfoImg;
        this.meetingPromiseImg = meetingPromiseImg;
        this.auctionType = AuctionType.PROGRESS;
    }
}
