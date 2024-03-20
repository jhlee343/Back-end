package shootingstar.var.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
public class Auction extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long auctionId;

    private String auctionUUID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @NotNull
    private User user;

    @Min(value = 100000)
    private long minBidAmount;

    @NotNull
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

    private String currentHighestBidderId;

    private long bidCount;

    @NotNull
    @Enumerated(value = EnumType.STRING)
    private AuctionType auctionType;

    @Builder
    public Auction(User user, long minBidAmount, LocalDateTime meetingDate, String meetingLocation,
                   String meetingInfoText, String meetingPromiseText, String meetingInfoImg, String meetingPromiseImg) {
        this.auctionUUID = UUID.randomUUID().toString();
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

    public void changeAuctionType(AuctionType auctionType) {
        this.auctionType = auctionType;
    }
}
