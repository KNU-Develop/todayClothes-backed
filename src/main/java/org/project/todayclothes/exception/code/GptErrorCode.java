package org.project.todayclothes.exception.code;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum GptErrorCode implements ErrorCode {
    GPT_INVALID_RESPONSE_FORMAT(HttpStatus.UNPROCESSABLE_ENTITY, "GPT 응답 형식이 잘못되었습니다."),
    GPT_RESPONSE_IS_NULL(HttpStatus.GATEWAY_TIMEOUT, "GPT 응답이 없습니다.")
    ;
    private final HttpStatus httpStatus;
    private final String message;

    GptErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
