package shootingstar.var.dto.req;

import lombok.Data;

@Data
public class BidReqDto {
    private String accessToken;
    private String auctionUUID;
    private long price;
}
