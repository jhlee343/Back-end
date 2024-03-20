package shootingstar.var.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentsInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    @Column(columnDefinition = "BINARY(16)")
    private UUID paymentUUID;

    private Long paymentAmount;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    public PaymentsInfo(UUID paymentUUID, Long paymentAmount, User user) {
        this.paymentUUID = UUID.randomUUID();
        this.paymentAmount = paymentAmount;
        this.user = user;
    }
}
