package shootingstar.var.dto.res;

import com.querydsl.core.annotations.QueryProjection;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserAuctionSuccessList {
    //참여중 진행중 경매 모두 사용
    //입찰 성공 - 이름, 약속 날짜, 최종 낙찰 금액, 낙찰자
    //참여중 - 이름, 남은 시간, 현재 입찰 금액, 입찰 수
    @NotNull
    private String vipUserName;

    @NotNull
    private LocalDateTime meetDate;

    @NotNull
    private String basicUserName;

    @NotNull
    private String profileImgUrl;

    @QueryProjection
    public UserAuctionSuccessList(String profileImgUrl,String vipUserName, LocalDateTime meetDate, String basicUserName){
        this.profileImgUrl = profileImgUrl;
        this.vipUserName = vipUserName;
        this.meetDate = meetDate;
        this.basicUserName = basicUserName;
    }
}
