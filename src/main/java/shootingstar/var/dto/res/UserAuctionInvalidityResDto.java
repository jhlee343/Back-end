package shootingstar.var.dto.res;

import com.querydsl.core.annotations.QueryProjection;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class UserAuctionInvalidityResDto {
    @NotNull
    private String profileImgUrl;

    @NotNull
    private String vipUserName;

    @NotNull
    private LocalDateTime auctionCreatedDate;

    @QueryProjection
    public UserAuctionInvalidityResDto(String profileImgUrl, String vipUserName, LocalDateTime auctionCreatedDate){
        this.profileImgUrl = profileImgUrl;
        this.vipUserName = vipUserName;
        this.auctionCreatedDate = auctionCreatedDate;;
    }
}
