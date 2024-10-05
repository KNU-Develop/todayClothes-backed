package org.project.todayclothes.exception.code;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum EventErrorCode implements ErrorCode {

    EVENT_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 이벤트를 찾을 수 없습니다."),
    INVALID_EVENT_DETAILS(HttpStatus.BAD_REQUEST, "이벤트 세부 사항이 유효하지 않습니다."),
    EVENT_CREATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "이벤트 생성 중 오류가 발생했습니다."),
    EVENT_UPDATE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "이벤트 업데이트 중 오류가 발생했습니다."),
    UNAUTHORIZED_EVENT_ACCESS(HttpStatus.FORBIDDEN, "해당 이벤트에 접근할 권한이 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    EventErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
