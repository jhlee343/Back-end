package shootingstar.var.dto.req;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import lombok.Data;
import shootingstar.var.annotation.After30Days;

@Data
public class AuctionCreateReqDto {
    @Min(value = 100000)
    private long minBidAmount;

    @After30Days
    private LocalDateTime meetingDate;

    @NotBlank
    private String meetingLocation;

    @NotBlank
    private String meetingInfoText;

    @NotBlank
    private String meetingPromiseText;

    private String meetingInfoImg;

    private String meetingPromiseImg;
}
