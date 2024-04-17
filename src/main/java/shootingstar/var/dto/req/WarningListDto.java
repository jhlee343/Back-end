package shootingstar.var.dto.req;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import shootingstar.var.entity.User;

import java.util.UUID;

@Data
public class WarningListDto {
    private String warningUUID;

    private String userUUID;

    private String warningContent;

    @QueryProjection
    public WarningListDto(String warningUUID, String userUUID, String warningContent){
        this.warningUUID = warningUUID;
        this.userUUID = userUUID;
        this.warningContent = warningContent;
    }
}
