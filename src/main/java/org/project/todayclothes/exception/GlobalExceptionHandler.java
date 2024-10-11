package org.project.todayclothes.exception;

import org.project.todayclothes.exception.code.ErrorCode;
import org.project.todayclothes.util.ApiResponseUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<?> handleCustomException(BusinessException ex) {
        ErrorCode errorCode = ex.getErrorCode();
        return ApiResponseUtil.createErrorResponse(errorCode);
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

