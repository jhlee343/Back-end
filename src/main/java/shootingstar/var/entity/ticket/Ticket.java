package shootingstar.var.entity.ticket;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shootingstar.var.entity.Auction;
import shootingstar.var.entity.BaseTimeEntity;
import shootingstar.var.entity.Review;
import shootingstar.var.entity.User;
import shootingstar.var.entity.chat.ChatRoom;

@Entity
@Getter
@NoArgsConstructor
public class Ticket extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ticketId;

    private String ticketUUID;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auction_id")
    private Auction auction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "winner_id")
    private User winner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizer_id")
    private User organizer;

    private boolean ticketIsOpened;
    private boolean winnerIsPushed;
    private boolean organizerIsPushed;

    @OneToMany(mappedBy = "ticket")
    private List<TicketMeetingTime> ticketMeetingTimes = new ArrayList<>();

    @OneToMany(mappedBy = "ticket")
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "ticket")
    private List<TicketReport> reports = new ArrayList<>();

    @OneToOne(mappedBy = "ticket")
    private ChatRoom chatRoom;


    @Builder
    public Ticket(Auction auction, User winner, User organizer) {
        this.ticketUUID = UUID.randomUUID().toString();
        this.auction = auction;
        this.winner = winner;
        this.organizer = organizer;
        this.ticketIsOpened = true;
        this.winnerIsPushed = false;
        this.organizerIsPushed = false;
    }

    public void changeTicketIsOpened(boolean ticketIsOpened) {
        this.ticketIsOpened = ticketIsOpened;
    }

    public void changeWinnerIsPushed(boolean winnerIsPushed) {
        this.winnerIsPushed = winnerIsPushed;
    }

    public void changeOrganizerIsPushed(boolean organizerIsPushed) {
        this.organizerIsPushed = organizerIsPushed;
    }
}
