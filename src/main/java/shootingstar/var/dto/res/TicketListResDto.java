package shootingstar.var.dto.res;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TicketListResDto {
    private String userName;
    private LocalDate meetingDate;
    private String meetingLocation;
    private Long userRating;
    private String profileImgUrl;

}
