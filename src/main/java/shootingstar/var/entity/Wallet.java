package shootingstar.var.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Entity
@Getter
public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long walletId;

    @Column(precision = 10, scale = 2)
    private BigDecimal totalDonationPrice;

    @Column(precision = 12, scale = 2)
    private BigDecimal currentCash;

    @Builder
    public Wallet() {
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
