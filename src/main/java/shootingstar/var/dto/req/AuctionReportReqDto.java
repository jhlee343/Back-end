package shootingstar.var.dto.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuctionReportReqDto {
    @NotBlank
    private String auctionUUID;

    @NotBlank
    private String auctionReportContent;
}
