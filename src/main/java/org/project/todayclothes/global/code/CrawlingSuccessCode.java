package org.project.todayclothes.global.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CrawlingSuccessCode {
    START_CRAWLING("크롤링 시작"),
    END_CRAWLING("크롤링 종료"),
    START_CRAWLING_ITEM_HEADER("상품 헤더 정보 크롤링 시작"),
    END_CRAWLING_ITEM_HEADER("상품 헤더 정보 크롤링 종료"),
    START_CRAWLING_ITEM_INFO("상품 상세 설명 크롤링 시작"),
    END_CRAWLING_ITEM_INFO("상품 상세 설명 크롤링 종료"),
    CHECKING_ITEM_LIST("제품 목록 확인 시작...")
    ;

    private final String message;
}