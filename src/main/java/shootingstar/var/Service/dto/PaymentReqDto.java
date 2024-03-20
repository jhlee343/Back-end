package shootingstar.var.Service.dto;

import lombok.Data;

@Data
public class PaymentReqDto {
    private String imp_uid;
    private String merchant_uid;
    private Long amount;
}
