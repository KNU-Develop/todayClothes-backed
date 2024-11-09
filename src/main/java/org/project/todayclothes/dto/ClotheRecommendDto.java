package org.project.todayclothes.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ClotheRecommendDto {
    private final String top;
    private final String bottom;
    private final String shoes;
    private final String acc1;
    private final String acc2;
    private final String comment;

    @Builder
    public ClotheRecommendDto(String top, String bottom, String shoes, String acc1, String acc2, String comment) {
        this.top = top;
        this.bottom = bottom;
        this.shoes = shoes;
        this.acc1 = acc1;
        this.acc2 = acc2;
        this.comment = comment;
    }
}
