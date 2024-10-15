package org.project.todayclothes.dto.gpt;

import lombok.Getter;

@Getter
public class GptResponseClotheRecommendDto {
    private String top;
    private String bottom;
    private String accessory;
    private String shoes;
    private String outerwear;
    private String message;
}
