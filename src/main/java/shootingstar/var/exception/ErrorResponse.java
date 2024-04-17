package shootingstar.var.exception;

import lombok.Data;

@Data
public class ErrorResponse {
    private String code;
    private String description;

    public ErrorResponse(ErrorCode errorCode) {
        this.code = errorCode.getCode();
        this.description = errorCode.getDescription();
    }
}
