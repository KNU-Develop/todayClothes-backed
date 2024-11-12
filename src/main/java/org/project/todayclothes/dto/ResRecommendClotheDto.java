package org.project.todayclothes.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ResRecommendClotheDto {
    private Long top;
    private Long bottom;
    private Long shoes;
    private Long outer;
    private Long acc1;
    private Long acc2;
    private String comment;
}