package shootingstar.var.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;

    @NotNull
    private UUID reviewUUID;

    private String reviewContent;

    private Double reviewRating;

    private Boolean isShowed;

    @ManyToOne
    @JoinColumn(name = "writer_id")
    private User writerId;

    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private User receiverId;

    private Long ticketId;

    @Builder
    public Review(UUID reviewUUID, User writerId, User receiverId, String reviewContent, double reviewRating, Boolean isShowed){
        this.reviewUUID = reviewUUID;
        this.receiverId = receiverId;
        this.writerId = writerId;
        this.reviewContent = reviewContent;
        this.reviewRating = reviewRating;
        this.isShowed =isShowed;
    }
}
