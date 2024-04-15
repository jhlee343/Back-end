package shootingstar.var.entity.log;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shootingstar.var.entity.BaseTimeEntity;

@Entity
@Getter
@NoArgsConstructor
public class DonationLog extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long donationLogId;

    private String donorNickname;

    @Column(precision = 10, scale = 2)
    private BigDecimal donationPrice;

    @Column(precision = 12, scale = 2)
    private BigDecimal totalDonationPrice;

    @Builder
    public DonationLog(String donorNickname, BigDecimal donationPrice, BigDecimal totalDonationPrice) {
        this.donorNickname = donorNickname;
        this.donationPrice = donationPrice;
        this.totalDonationPrice = totalDonationPrice;
    }
}
