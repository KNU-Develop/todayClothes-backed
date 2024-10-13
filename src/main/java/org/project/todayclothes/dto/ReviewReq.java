package org.project.todayclothes.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.project.todayclothes.global.Feedback;

@Getter
@NoArgsConstructor
public class ReviewReq {
    private Long clothesId;
    private Feedback feedback;

    @Builder
    public ReviewReq(Long clothesId, Feedback feedback) {
        this.clothesId = clothesId;
        this.feedback = feedback;
    }

}
