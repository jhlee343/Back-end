package shootingstar.var.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentsInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    private String paymentUUID;

    private Long paymentAmount;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    public PaymentsInfo(String paymentUUID, Long paymentAmount, User user) {
        this.paymentUUID = paymentUUID;
        this.paymentAmount = paymentAmount;
        this.user = user;
    }
}
