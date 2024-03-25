package shootingstar.var.dto.req;

import lombok.Data;

@Data
public class ExchangeReqDto {
    private String exchangeBank;
    private String exchangeAccount;
    private String exchangeAccountHolder;
    private Long exchangePoint;
}
