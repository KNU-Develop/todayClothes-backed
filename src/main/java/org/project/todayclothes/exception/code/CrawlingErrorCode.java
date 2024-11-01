package org.project.todayclothes.exception.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CrawlingErrorCode {
    URL_CONNECT_FAIL("크롤링 대상 페이지 접속 실패"),
    ELEMENT_NOT_FOUND("크롤링 대상 요소를 찾을 수 없음"),
    CONTENT_LOAD_TIMEOUT("콘텐츠 로딩 시간 초과"),
    ELEMENT_NOT_FOUND_FIND_BREAK("다음 대상 탐색을 위한 크롤링 대상 요로를 찾을 수 없음"),
    INFINITE_FIND_BREAK("비정상적인 무한 탐색 종료");

    private final String message;
}