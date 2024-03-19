package shootingstar.var.dto.req;

import lombok.Data;
import shootingstar.var.entity.User;

import java.util.UUID;

@Data
public class WarningListDto {
    private UUID warningUUID;

    private User userUUID;

    private String warningContent;
}
