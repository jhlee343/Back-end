package shootingstar.var.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;
import shootingstar.var.entity.ticket.Ticket;

@Entity
@Getter
@NoArgsConstructor
public class Review extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;

    private String reviewUUID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id")
    private User writer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    private User receiver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id")
    private Ticket ticket;

    @NotBlank
    private String reviewContent;

    private double reviewRating;

    private boolean isShowed;

    @Builder
    public Review(User writer, User receiver, Ticket ticket, String reviewContent,
                  double reviewRating) {
        this.reviewUUID = UUID.randomUUID().toString();
        this.writer = writer;
        this.receiver = receiver;
        this.ticket = ticket;
        this.reviewContent = reviewContent;
        this.reviewRating = reviewRating;
        this.isShowed = true;
    }
}
