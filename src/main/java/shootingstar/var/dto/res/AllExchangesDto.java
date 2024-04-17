package shootingstar.var.dto.res;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import shootingstar.var.enums.status.ExchangeStatus;

import java.time.LocalDateTime;

@Data
public class AllExchangesDto {
    private String exchangeUUID;
    private String name;
    private String nickname;
    private Long exchangePoint;
    private String exchangeBank;
    private String exchangeAccount;
    private String exchangeAccountHolder;
    private LocalDateTime createdTime;

    @QueryProjection
    public AllExchangesDto(String exchangeUUID, String name, String nickname, Long exchangePoint, String exchangeBank, String exchangeAccount, String exchangeAccountHolder, LocalDateTime createdTime) {
        this.exchangeUUID = exchangeUUID;
        this.name = name;
        this.nickname = nickname;
        this.exchangePoint = exchangePoint;
        this.exchangeBank = exchangeBank;
        this.exchangeAccount = exchangeAccount;
        this.exchangeAccountHolder = exchangeAccountHolder;
        this.createdTime = createdTime;
    }
}