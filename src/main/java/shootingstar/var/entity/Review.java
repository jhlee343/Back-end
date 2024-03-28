package shootingstar.var.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;
import shootingstar.var.entity.ticket.Ticket;

@Entity
@Getter
@NoArgsConstructor
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;

    @NotNull
    private String reviewUUID;

    private String reviewContent;
    private Double reviewRating;

    private Boolean isShowed;

    @ManyToOne
    @JoinColumn(name = "writer_id")
    private User writerId;

    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private User receiverId;

    @ManyToOne
    @JoinColumn(name = "ticket_id")
    private Ticket ticketId;



    public Review(User writerId, User receiverId, String reviewContent,
                  double reviewRating, Ticket ticketId, Boolean isShowed){
        this.reviewUUID = UUID.randomUUID().toString();
        this.receiverId = receiverId;
        this.writerId = writerId;
        this.reviewContent = reviewContent;
        this.reviewRating = reviewRating;
        this.ticketId = ticketId;
        this.isShowed =isShowed;
    }
}
