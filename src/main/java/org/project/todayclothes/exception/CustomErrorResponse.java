package org.project.todayclothes.exception;

import org.springframework.http.HttpStatus;

public class CustomErrorResponse {
    private final HttpStatus status;
    private final String message;

    public CustomErrorResponse(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
