package shootingstar.var.dto.res;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class TicketListResDto {
    private String userName;
    private LocalDateTime meetingDate;
    private String meetingLocation;
    private Double userRating;
    private String profileImgUrl;

    @QueryProjection
    public TicketListResDto(String userName, LocalDateTime meetingDate, Double userRating, String profileImgUrl) {
        this.userName = userName;
        this.meetingDate = meetingDate;
//        this.meetingLocation = meetingLocation;
        this.userRating = userRating;
        this.profileImgUrl = profileImgUrl;
    }

}
