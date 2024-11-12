package org.project.todayclothes.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
public class ResFinalRecommendClotheDto {
    private String recommendClotheUrl;
    private String comment;

    @Builder
    public ResFinalRecommendClotheDto(String recommendClotheUrl, String comment) {
        this.recommendClotheUrl = recommendClotheUrl;
        this.comment = comment;
    }
}
