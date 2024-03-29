package shootingstar.var.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;


@Getter
public enum ErrorCode {
    SERVER_ERROR(INTERNAL_SERVER_ERROR, "0000", "알 수 없는 오류가 발생했습니다."),
    INCORRECT_FORMAT(BAD_REQUEST, "0001", "잘못된 입력입니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED,"0002", "제공되지 않는 메서드입니다."),

    AUTHENTICATION_ERROR(UNAUTHORIZED, "0100", "인증에 실패하였습니다."),
    ACCESS_DENIED(FORBIDDEN, "0101", "접근 권한이 없습니다."),
    INVALID_ACCESS_TOKEN(FORBIDDEN, "0102", "잘못된 Access Token 입니다."),
    EXPIRED_ACCESS_TOKEN(FORBIDDEN, "0103", "만료된 Access Token 입니다."),
    UNSUPPORTED_ACCESS_TOKEN(FORBIDDEN, "0104", "지원하지 않는 Access Token 입니다."),
    ILLEGAL_ACCESS_TOKEN(FORBIDDEN, "0105", "권한 정보가 없는 Access Token 입니다."),
    INVALID_REFRESH_TOKEN(FORBIDDEN, "0106", "잘못된 Refresh Token 입니다."),
    EXPIRED_REFRESH_TOKEN(FORBIDDEN, "0107", "만료된 Refresh Token 입니다."),
    UNSUPPORTED_REFRESH_TOKEN(FORBIDDEN, "0108", "지원하지 않는 Refresh Token 입니다."),
    ILLEGAL_REFRESH_TOKEN(FORBIDDEN, "0109", "권한 정보가 없는 Refresh Token 입니다."),

    KAKAO_AUTHENTICATION_ERROR(UNAUTHORIZED, "0110", "카카오로부터 ACCESS 토큰 획득에 실패했습니다."),
    KAKAO_CONNECT_FAILED_TOKEN_ENDPOINT(UNAUTHORIZED, "0111", "카카오 토큰 엔드포인트와 통신에 실패하였습니다."),
    KAKAO_FAILED_GET_USERINFO_ERROR(UNAUTHORIZED, "0112", "카카오로부터 사용자 정보를 가져오지 못했습니다."),
    KAKAO_CONNECT_FAILED_USERINFO_ENDPOINT(UNAUTHORIZED, "0113", "카카오 사용자 정보 엔드포인트와 통신에 실패하였습니다."),
    KAKAO_CONNECT_FAILED_UNLINK_ENDPOINT(UNAUTHORIZED, "0114", "카카오 사용자 연결 해제 엔드포인트와 통신에 실패하였습니다."),
    LOGGED_IN_SOMEWHERE_ELSE(FORBIDDEN, "0115", "다른 장소에서 로그인 되었습니다."),

    NOT_FOUND_END_POINT(NOT_FOUND, "0200", "존재하지 않는 접근입니다."),

    INCORRECT_FORMAT_EMAIL(BAD_REQUEST, "1001", "잘못된 형식의 이메일입니다."),
    INCORRECT_FORMAT_CODE(BAD_REQUEST, "1002", "잘못된 형식의 인증코드입니다."),
    INCORRECT_FORMAT_NICKNAME(BAD_REQUEST, "1003", "잘못된 형식의 닉네임입니다."),
    INCORRECT_FORMAT_KAKAO_ID(BAD_REQUEST, "1004", "잘못된 형식의 카카오 고유번호입니다."),
    INCORRECT_FORMAT_USER_NAME(BAD_REQUEST, "1005", "잘못된 형식의 사용자 이름입니다."),
    INCORRECT_FORMAT_PHONE_NUMBER(BAD_REQUEST, "1006", "잘못된 형식의 휴대폰 번호입니다."),
    INCORRECT_FORMAT_PROFILE_IMG_URL(BAD_REQUEST, "1007", "잘못된 형식의 프로필 이미지 주소입니다."),

    AUTH_ERROR_EMAIL(UNAUTHORIZED, "1101", "잘못된 키 혹은 잘못(만료) 된 인증 코드입니다."),
    VALIDATE_ERROR_EMAIL(UNAUTHORIZED, "1102", "인증이 만료되었거나 인증되지 않은 이메일입니다."),
    BANNED_USER(UNAUTHORIZED, "1103", "경고 3회 누적으로 정지된 사용자입니다."),

    USER_NOT_FOUND(NOT_FOUND, "1201", "존재하지 않는 사용자입니다."),

    DUPLICATE_EMAIL(CONFLICT, "1301", "이미 사용중인 이메일입니다."),
    DUPLICATE_NICKNAME(CONFLICT, "1302", "이미 사용중인 닉네임입니다."),
    WITHDRAWAL_ERROR_BY_AUCTION_IN_PROGRESS(CONFLICT, "1303", "현재 진행중인 경매가 존재할 경우 회원탈퇴가 불가능합니다."),
    WITHDRAWAL_ERROR_BY_TICKET_IN_PROGRESS(CONFLICT, "1304", "현재 종료되지 않은 식사권이 존재할 경우 회원탈퇴가 불가능합니다."),

    MIN_BID_AMOUNT_INCORRECT_FORMAT(BAD_REQUEST, "2000", "최소입찰금액은 자신의 보유 포인트보다 적어야 합니다."),
    INCORRECT_FORMAT_MIN_BID_AMOUNT(BAD_REQUEST, "2001", "잘못된 형식의 최소 입찰 금액입니다."),
    INCORRECT_FORMAT_MEETING_DATE(BAD_REQUEST, "2002", "잘못된 형식의 식사 날짜입니다."),
    INCORRECT_FORMAT_MEETING_LOCATION(BAD_REQUEST, "2003", "잘못된 형식의 식사 장소입니다."),
    INCORRECT_FORMAT_MEETING_INFO_TEXT(BAD_REQUEST, "2004", "잘못된 형식의 만남에 대한 정보입니다."),
    INCORRECT_FORMAT_MEETING_PROMISE_TEXT(BAD_REQUEST, "2005", "잘못된 형식의 만남에 대한 약속입니다."),

    AUCTION_ACCESS_DENIED(FORBIDDEN, "2100", "접근 권한이 없습니다."),
    AUCTION_NOT_FOUND(NOT_FOUND, "2200", "존재하지 않는 경매입니다."),
    AUCTION_CONFLICT(CONFLICT, "2300", "이미 처리된 경매입니다."),

    EXCHANGE_AMOUNT_INCORRECT_FORMAT(BAD_REQUEST, "3000", "환전 포인트는 보유 포인트보다 적어야 합니다."),
    PAYMENT_ACCESS_DENIED(FORBIDDEN, "3100", "결제 정보가 다릅니다."),
    DIFFERENT_ACCOUNT_HOLDER(FORBIDDEN, "3100", "본인 명의의 계좌가 아닙니다."),

    SCHEDULING_SERVER_ERROR(INTERNAL_SERVER_ERROR, "4000", "알 수 없는 오류가 발생했습니다."),
    TASK_NOT_FOUND(NOT_FOUND, "4200", "존재하지 않는 task입니다."),
    FAIL_TASK_DELETE(NOT_FOUND, "4201", "스케줄링된 task를 취소하는데 실패했습니다."),

    REVIEW_NOT_FOUND(NOT_FOUND, "5201","존재하지 않는 리뷰입니다."),

    INCORRECT_FORMAT_TICKET_ID(BAD_REQUEST, "6001", "잘못된 형식의 식사권 고유번호입니다."),
    INCORRECT_FORMAT_START_MEETING_TIME(BAD_REQUEST, "6002", "잘못된 형식의 만남 시작 시간입니다."),
    INCORRECT_FORMAT_TICKET_REPORT_CONTENT(BAD_REQUEST, "6003", "잘못된 형식의 식사권 신고 내용입니다."),
    INCORRECT_FORMAT_TICKET_REPORT_EVIDENCE_URL(BAD_REQUEST, "6004", "잘못된 형식의 식사권 신고 증거 URL입니다."),
    INCORRECT_FORMAT_REVIEW_CONTENT(BAD_REQUEST, "6005", "잘못된 형식의 리뷰 내용입니다."),
    INCORRECT_FORMAT_REVIEW_RATING(BAD_REQUEST, "6006", "잘못된 형식의 리뷰 점수입니다."),
    MEETING_TIME_NOT_PASSED(BAD_REQUEST, "6007", "만남이 끝나기 전에 리뷰 작성을 할 수 없습니다."),

    TICKET_NOT_FOUND(NOT_FOUND, "6200", "존재하지 않는 식사권입니다."),
    TICKET_MEETING_TIME_NOT_FOUND(NOT_FOUND, "6201", "존재하지 않는 만남 시작 시간입니다."),

    TICKET_MEETING_TIME_CONFLICT(CONFLICT, "6300", "이미 처리된 식사권 만남 시간입니다."),
    TICKET_REPORT_CONFLICT(CONFLICT, "6301", "이미 신고된 식사권입니다."),
    TICKET_CANCEL_CONFLICT(CONFLICT, "6302", "식사 시간이 지난 후에는 취소가 불가능합니다."),
    ALREADY_TICKET_CANCEL_CONFLICT(CONFLICT, "6303", "이미 취소된 식사권입니다."),
    REVIEW_CONFLICT(CONFLICT, "6304", "해당 식사권에 대한 리뷰를 작성한 적이 있습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String description;

    ErrorCode(HttpStatus httpStatus, String code, String description) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.description = description;
    }
}
