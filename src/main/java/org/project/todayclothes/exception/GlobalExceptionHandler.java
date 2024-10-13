package org.project.todayclothes.exception;

import lombok.extern.slf4j.Slf4j;
import org.project.todayclothes.exception.code.ErrorCode;
import org.project.todayclothes.util.ApiResponseUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MultipartException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<?> handleCustomException(BusinessException ex) {
        ErrorCode errorCode = ex.getErrorCode();
        return ApiResponseUtil.createErrorResponse(errorCode);
    }

    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<String> handleMultipartException(MultipartException ex) {
        log.error("Multipart Exception occurred", ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneralException(Exception ex) {
        if (ex instanceof BusinessException) {
            ErrorCode errorCode = ((BusinessException) ex).getErrorCode();
            return ApiResponseUtil.createErrorResponse(errorCode);
        } else {
            return ApiResponseUtil.createErrorResponse("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
}

