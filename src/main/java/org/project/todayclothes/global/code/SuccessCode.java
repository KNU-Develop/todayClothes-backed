package org.project.todayclothes.global.code;

import org.springframework.http.HttpStatus;

public interface SuccessCode {
    String name();
    HttpStatus getHttpStatus();
    String getMessage();
}
