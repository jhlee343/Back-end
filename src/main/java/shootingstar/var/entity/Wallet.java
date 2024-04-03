package shootingstar.var.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Getter
public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long walletId;

    private String walletUUID;

    @Column(precision = 10, scale = 2)
    private BigDecimal totalDonationPrice;

    @Column(precision = 12, scale = 2)
    private BigDecimal currentCash;

    @Builder
    public Wallet() {
        this.walletUUID = UUID.randomUUID().toString();
        this.totalDonationPrice = new BigDecimal(0);
        this.currentCash = new BigDecimal(0);
    }

    public void increaseDonation(BigDecimal point) {
        this.totalDonationPrice = this.totalDonationPrice.add(point);
    }

    public void increaseCash(BigDecimal point) {
        this.currentCash = this.currentCash.add(point);
    }

    public void decreaseCash(BigDecimal point) {
        this.currentCash = this.currentCash.subtract(point);
    }
}
