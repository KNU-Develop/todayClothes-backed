package org.project.todayclothes.exception.code;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum CommonErrorCode implements ErrorCode {
    // 인증 관련 에러
    NO_REFRESH_TOKEN(HttpStatus.BAD_REQUEST, "리프레쉬 토큰이 요청에 포함되지 않았습니다."),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "토큰이 만료되었습니다."),
    NOT_HAVE_ACCESS_TOKEN(HttpStatus.BAD_REQUEST, "엑세스 토큰이 없습니다."),
    NOT_VALID_TOKEN(HttpStatus.UNAUTHORIZED, "해당 토큰은 유효한 토큰이 아닙니다."),
    NOT_EXISTS_AUTHORIZATION(HttpStatus.UNAUTHORIZED, "Authorization Header가 빈 값입니다."),
    NOT_VALID_BEARER_GRANT_TYPE(HttpStatus.UNAUTHORIZED, "인증 타입이 Bearer 타입이 아닙니다."),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "해당 refresh token은 존재하지 않습니다."),
    REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "해당 refresh token은 만료됐습니다."),
    NOT_ACCESS_TOKEN_TYPE(HttpStatus.UNAUTHORIZED, "해당 토큰은 ACCESS TOKEN이 아닙니다."),
    NOT_REFRESH_TOKEN_TYPE(HttpStatus.UNAUTHORIZED, "해당 토큰은 REFRESH TOKEN이 아닙니다."),
    NO_PERMISSION(HttpStatus.UNAUTHORIZED, "권한 없음"),
    FORBIDDEN_ROLE(HttpStatus.FORBIDDEN, "해당 Role이 아닙니다."),
    UNAUTHORIZED_MEMBER(HttpStatus.UNAUTHORIZED, "회원 정보가 일치하지 않습니다."),

    // 데이터 관련 에러
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "입력 값이 유효하지 않습니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "허용되지 않은 HTTP 메서드입니다."),
    REQUEST_NOT_READABLE(HttpStatus.BAD_REQUEST, "요청 본문을 읽을 수 없습니다."),
    MISSING_REQUEST_PARAMETER(HttpStatus.BAD_REQUEST, "필수 요청 파라미터가 누락되었습니다."),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "요청한 리소스를 찾을 수 없습니다."),

    // 서버 관련 에러
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다."),
    SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "서비스를 사용할 수 없습니다."),
    TIMEOUT(HttpStatus.REQUEST_TIMEOUT, "서버 응답 시간이 초과되었습니다."),

    FILE_CONVERT_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "파일 변환 오류가 발생했습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    CommonErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
