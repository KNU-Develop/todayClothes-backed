package org.project.todayclothes.util;

import org.project.todayclothes.exception.code.Api_Response;
import org.project.todayclothes.exception.code.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ApiResponseUtil {

    public static <T> ResponseEntity<Api_Response<T>> createResponse(int code, String message, T result) {
        Api_Response<T> response = Api_Response.<T>builder()
                .code(code)
                .message(message)
                .result(result)
                .build();
        return ResponseEntity.status(code).body(response);
    }

    public static <T> ResponseEntity<Api_Response<T>> createSuccessResponse(String message, T result) {
        return createResponse(HttpStatus.OK.value(), message, result);
    }

    public static <T> ResponseEntity<Api_Response<T>> createSuccessResponse(String message) {
        return createResponse(HttpStatus.OK.value(), message, null);
    }

    public static <T> ResponseEntity<Api_Response<T>> createErrorResponse(ErrorCode errorCode) {
        return createResponse(errorCode.getHttpStatus().value(), errorCode.getMessage(), null);
    }

    public static <T> ResponseEntity<Api_Response<T>> createErrorResponse(String message, int code) {
        return createResponse(code, message, null);
    }

    public static <T> ResponseEntity<Api_Response<T>> createBadRequestResponse(String message) {
        return createResponse(HttpStatus.BAD_REQUEST.value(), message, null);
    }

    public static <T> ResponseEntity<Api_Response<T>> createForbiddenResponse(String message) {
        return createResponse(HttpStatus.FORBIDDEN.value(), message, null);
    }

    public static <T> ResponseEntity<Api_Response<T>> createUnAuthorization() {
        return createResponse(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase(), null);
    }

    public static <T> ResponseEntity<Api_Response<T>> createNotFoundResponse(String message) {
        return createResponse(HttpStatus.NOT_FOUND.value(), message, null);
    }
}
