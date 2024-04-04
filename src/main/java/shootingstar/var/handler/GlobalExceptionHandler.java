package shootingstar.var.handler;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import shootingstar.var.exception.CustomException;
import shootingstar.var.exception.ErrorCode;
import shootingstar.var.exception.ErrorResponse;

import java.util.HashMap;
import java.util.Map;

import static shootingstar.var.exception.ErrorCode.*;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();

        ErrorCode errorCode = INCORRECT_FORMAT;
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            switch (fieldError.getField()) {
                case "email" -> {
                    errorCode = INCORRECT_FORMAT_EMAIL;
                    break;
                } case "kakaoId" -> {
                    errorCode = INCORRECT_FORMAT_KAKAO_ID;
                    break;
                } case "nickname" -> {
                    errorCode = INCORRECT_FORMAT_NICKNAME;
                    break;
                } case "userName" -> {
                    errorCode = INCORRECT_FORMAT_USER_NAME;
                    break;
                } case "phoneNumber" -> {
                    errorCode = INCORRECT_FORMAT_PHONE_NUMBER;
                    break;
                } case "profileImgUrl" -> {
                    errorCode = INCORRECT_FORMAT_PROFILE_IMG_URL;
                    break;
                } case "minBidAmount" -> {
                    errorCode = INCORRECT_FORMAT_MIN_BID_AMOUNT;
                    break;
                } case "meetingDate" -> {
                    errorCode = INCORRECT_FORMAT_MEETING_DATE;
                    break;
                } case "meetingLocation" -> {
                    errorCode = INCORRECT_FORMAT_MEETING_LOCATION;
                    break;
                } case "meetingInfoText" -> {
                    errorCode = INCORRECT_FORMAT_MEETING_INFO_TEXT;
                    break;
                } case "meetingPromiseText" -> {
                    errorCode = INCORRECT_FORMAT_MEETING_PROMISE_TEXT;
                    break;
                } case "ticketUUID" -> {
                    errorCode = INCORRECT_FORMAT_TICKET_UUID;
                    break;
                } case "startMeetingTime" -> {
                    errorCode = INCORRECT_FORMAT_START_MEETING_TIME;
                    break;
                } case "ticketReportContent" -> {
                    errorCode = INCORRECT_FORMAT_TICKET_REPORT_CONTENT;
                    break;
                } case "ticketReportEvidenceUrl" -> {
                    errorCode = INCORRECT_FORMAT_TICKET_REPORT_EVIDENCE_URL;
                    break;
                }  case "adminLoginId" -> {
                    errorCode = INCORRECT_FORMAT_ADMIN_ID;
                    break;
                } case "adminPassword" -> {
                    errorCode = INCORRECT_FORMAT_ADMIN_PASSWORD;
                    break;
                } case "adminNickname" -> {
                    errorCode = INCORRECT_FORMAT_ADMIN_NICKNAME;
                    break;
                } case "adminSecretKey" -> {
                    errorCode = INCORRECT_FORMAT_ADMIN_SECRET_KEY;
                    break;
                }
            }

            if (!errorCode.equals(INCORRECT_FORMAT)) {
                break;
            }
        }

        return ResponseEntity.status(errorCode.getHttpStatus()).body(new ErrorResponse(errorCode));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException exception) {
        Map<String, String> fieldErrors = new HashMap<>();

        // 각 제약 조건 위반을 순회하며 필드 이름과 오류 메시지를 맵에 저장
        exception.getConstraintViolations().forEach(violation -> {
            String field = violation.getPropertyPath().toString();
            String message = violation.getMessage();
            fieldErrors.put(field, message);
        });

        ErrorCode errorCode = INCORRECT_FORMAT;

        for (Map.Entry<String, String> entry : fieldErrors.entrySet()) {
            String fieldName = entry.getKey();

            if (fieldName.contains("nickname")) {
                errorCode = INCORRECT_FORMAT_NICKNAME;
            } else if (fieldName.contains("email")) {
                errorCode = INCORRECT_FORMAT_EMAIL;
            } else if (fieldName.contains("vipUUID") || fieldName.contains("userUUID")) {
                errorCode = INCORRECT_FORMAT_USER_UUID;
            } else if (fieldName.contains("auctionUUID")) {
                errorCode = INCORRECT_FORMAT_AUCTION_UUID;
            } else if (fieldName.contains("vipInfoUUID")) {
                errorCode = INCORRECT_FORMAT_VIP_INFO_UUID;
            }

            if (!errorCode.equals(INCORRECT_FORMAT)) {
                break;
            }
        }
        return ResponseEntity.status(errorCode.getHttpStatus()).body(new ErrorResponse(errorCode));
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomExceptionHandler(CustomException exception) {
        ErrorCode errorCode = exception.getErrorCode();
        ErrorResponse errorResponse = new ErrorResponse(errorCode);
        log.info(exception.getMessage());
        return ResponseEntity.status(errorCode.getHttpStatus()).body(errorResponse);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleCustomExceptionHandler(NoHandlerFoundException exception) {
        log.info(exception.getMessage());
        ErrorCode errorCode = NOT_FOUND_END_POINT;
        ErrorResponse errorResponse = new ErrorResponse(errorCode);
        return ResponseEntity.status(errorCode.getHttpStatus()).body(errorResponse);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleCustomExceptionHandler(HttpMessageNotReadableException exception) {
        log.info(exception.getMessage());
        ErrorCode errorCode = INCORRECT_FORMAT;
        ErrorResponse errorResponse = new ErrorResponse(errorCode);
        return ResponseEntity.status(errorCode.getHttpStatus()).body(errorResponse);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleCustomExceptionHandler(HttpRequestMethodNotSupportedException exception) {
        log.info(exception.getMessage());
        ErrorCode errorCode = METHOD_NOT_ALLOWED;
        ErrorResponse errorResponse = new ErrorResponse(errorCode);
        return ResponseEntity.status(errorCode.getHttpStatus()).body(errorResponse);
    }

    @ExceptionHandler(InternalError.class)
    public ResponseEntity<ErrorResponse> handleCustomExceptionHandler(InternalError exception) {
        log.info(exception.getMessage());
        ErrorCode errorCode = SERVER_ERROR;
        ErrorResponse errorResponse = new ErrorResponse(errorCode);
        return ResponseEntity.status(errorCode.getHttpStatus()).body(errorResponse);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleCustomExceptionHandler(RuntimeException exception) {
        log.info(exception.getMessage());
        ErrorCode errorCode = SERVER_ERROR;
        ErrorResponse errorResponse = new ErrorResponse(errorCode);
        return ResponseEntity.status(errorCode.getHttpStatus()).body(errorResponse);
    }
}
