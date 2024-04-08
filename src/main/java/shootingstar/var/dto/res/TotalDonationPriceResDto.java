package shootingstar.var.dto.res;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TotalDonationPriceResDto {
    private BigDecimal totalDonationPrice;

    public TotalDonationPriceResDto(BigDecimal totalDonationPrice) {
        this.totalDonationPrice = totalDonationPrice;
    }
}
