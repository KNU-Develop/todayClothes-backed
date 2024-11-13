package org.project.todayclothes.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ResRecommendClotheDto {
    @JsonProperty("TOP")
    private Long top;

    @JsonProperty("BOTTOM")
    private Long bottom;

    @JsonProperty("SHOES")
    private Long shoes;

    @JsonProperty("OUTER")
    private Long outer;

    @JsonProperty("ACC1")
    private Long acc1;

    @JsonProperty("ACC2")
    private Long acc2;

    @JsonProperty("Comment")
    private String comment;
}
