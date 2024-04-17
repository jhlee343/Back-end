package shootingstar.var.dto.req;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReviewSaveReqDto {
    @NotNull
    private String ticketUUID;

    @NotBlank
    private String reviewContent;

    @Min(value = 1)
    @Max(value = 5)
    private int reviewRating;
}
