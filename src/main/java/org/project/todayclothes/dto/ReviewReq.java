package org.project.todayclothes.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.project.todayclothes.entity.Review;
import org.project.todayclothes.global.Feedback;

@Getter
@NoArgsConstructor
public class ReviewReq {
    private Feedback feedback;
    private String imageFile;

    public ReviewReq(Feedback feedback, String imageFile) {
        this.feedback = feedback;
        this.imageFile = imageFile;
    }

}
