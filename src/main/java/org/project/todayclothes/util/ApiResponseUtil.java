package org.project.todayclothes.util;

import org.project.todayclothes.exception.Api_Response;
import org.project.todayclothes.global.code.SuccessCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.project.todayclothes.exception.code.ErrorCode;

public class ApiResponseUtil {

    public static <T> ResponseEntity<Api_Response<T>> createResponse(HttpStatus status, String message, T result) {
        Api_Response<T> response = Api_Response.<T>builder()
                .code(status.value())
                .message(message)
                .result(result)
                .build();
        return ResponseEntity.status(status).body(response);
    }

    public static <T> ResponseEntity<Api_Response<T>> createSuccessResponse(SuccessCode successCode, T result) {
        return createResponse(successCode.getHttpStatus(), successCode.getMessage(), result);
    }

    public static <T> ResponseEntity<Api_Response<T>> createSuccessResponse(String message, T result) {
        return createResponse(HttpStatus.OK, message, result);  // 200 OK 반환
    }

    public static <T> ResponseEntity<Api_Response<T>> createSuccessResponse(String message) {
        return createResponse(HttpStatus.OK, message, null);    // 200 OK 반환
    }

    public static <T> ResponseEntity<Api_Response<T>> createErrorResponse(ErrorCode errorCode) {
        return createResponse(errorCode.getHttpStatus(), errorCode.getMessage(), null);
    }

    public static <T> ResponseEntity<Api_Response<T>> createErrorResponse(String message, int code) {
        return createResponse(HttpStatus.valueOf(code), message, null);
    }

    public static <T> ResponseEntity<Api_Response<T>> createBadRequestResponse(String message) {
        return createResponse(HttpStatus.BAD_REQUEST, message, null);
    }

    public static <T> ResponseEntity<Api_Response<T>> createForbiddenResponse(String message) {
        return createResponse(HttpStatus.FORBIDDEN, message, null);
    }

    public static <T> ResponseEntity<Api_Response<T>> createUnAuthorization() {
        return createResponse(HttpStatus.UNAUTHORIZED, HttpStatus.UNAUTHORIZED.getReasonPhrase(), null);
    }

    public static <T> ResponseEntity<Api_Response<T>> createNotFoundResponse(String message) {
        return createResponse(HttpStatus.NOT_FOUND, message, null);
    }
}
