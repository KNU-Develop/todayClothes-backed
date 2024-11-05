package org.project.todayclothes.dto.gpt;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class GptResClotheRecommendDto {
    private Long top;
    private Long bottom;
    private Long shoes;
    private Long acc1;
    private Long acc2;
    private Long outer;
    private String comment;
}
