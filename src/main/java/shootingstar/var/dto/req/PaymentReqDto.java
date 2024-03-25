package shootingstar.var.dto.req;

import lombok.Data;

@Data
public class PaymentReqDto {
    private String imp_uid;
    private String merchant_uid;
    private Long paymentAmount;
}
