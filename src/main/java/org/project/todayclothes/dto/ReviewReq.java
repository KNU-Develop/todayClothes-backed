package org.project.todayclothes.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.project.todayclothes.entity.Review;
import org.project.todayclothes.global.Feedback;

@Getter
@NoArgsConstructor
public class ReviewReq {
    private Feedback feedback;
    private String imageFile;
}
