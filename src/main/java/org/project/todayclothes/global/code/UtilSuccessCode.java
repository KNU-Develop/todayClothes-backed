package org.project.todayclothes.global.code;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum UtilSuccessCode implements SuccessCode {
    GPT_RESPONSE(HttpStatus.OK, "GPT 응답 성공")
    ;

    private final HttpStatus httpStatus;
    private final String message;

    UtilSuccessCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
