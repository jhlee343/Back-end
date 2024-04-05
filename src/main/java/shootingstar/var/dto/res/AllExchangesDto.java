package shootingstar.var.dto.res;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import shootingstar.var.enums.status.ExchangeStatus;

@Data
public class AllExchangesDto {
    private String exchangeUUID;
    private String nickname;
    private String name;
    private Long exchangePoint;
    private String exchangeBank;
    private String exchangeAccount;
    private ExchangeStatus exchangeStatus;

    @QueryProjection
    public AllExchangesDto(String exchangeUUID, String nickname, String name, Long exchangePoint, String exchangeBank, String exchangeAccount, ExchangeStatus exchangeStatus) {
        this.exchangeUUID = exchangeUUID;
        this.nickname = nickname;
        this.name = name;
        this.exchangePoint = exchangePoint;
        this.exchangeBank = exchangeBank;
        this.exchangeAccount = exchangeAccount;
        this.exchangeStatus = exchangeStatus;
    }
}