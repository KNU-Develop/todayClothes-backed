package org.project.todayclothes.exception.code;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ClotheErrorCode implements ErrorCode{
    CLOTHES_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 옷을 찾을 수 없습니다."),
    S3_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "S3에 파일을 업로드하는 중 오류가 발생했습니다."),
    S3_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "S3에 파일을 삭제하는 중 오류가 발생했습니다."),
    FILE_CONVERT_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "파일 변환 오류가 발생했습니다."),
    BACKGROUND_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "배경 제거 중 오류가 발생했습니다."),
    PYTHON_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Python 서버 오류가 발생했습니다."),
    INVALID_URL_FORMAT(HttpStatus.BAD_REQUEST, "유효하지 않은 URL 형식입니다.");

    private final HttpStatus httpStatus;
    private final String message;

    ClotheErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
