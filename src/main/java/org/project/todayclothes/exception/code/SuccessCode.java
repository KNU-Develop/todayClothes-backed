package org.project.todayclothes.exception.code;

import lombok.Getter;

@Getter
public enum SuccessCode {

    /**
     * ******************************* Success CodeList ***************************************
     */
    SELECT_SUCCESS(200, "200", "SELECT SUCCESS"),
    DELETE_SUCCESS(200, "200", "DELETE SUCCESS"),
    INSERT_SUCCESS(200, "200", "INSERT SUCCESS"),
    UPDATE_SUCCESS(200, "200", "UPDATE SUCCESS"),
    PARTIAL_SUCCESS(206, "206", "REQUEST PARTIAL_SUCCESS")
    ; // End

    /**
     * ******************************* Success Code Constructor ***************************************
     */
    // 성공 코드의 '코드 상태'를 반환한다.
    private final int status;

    // 성공 코드의 '코드 값'을 반환한다.
    private final String code;

    // 성공 코드의 '코드 메시지'를 반환한다.s
    private final String message;

    // 생성자 구성
    SuccessCode(final int status, final String code, final String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
