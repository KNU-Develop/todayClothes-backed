package org.project.todayclothes.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.project.todayclothes.exception.BusinessException;
import org.project.todayclothes.exception.code.ReviewErrorCode;
import org.project.todayclothes.global.Feedback;


@Entity
@Getter
@NoArgsConstructor
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Feedback feedback;
    private String imageFile;

    @OneToOne(mappedBy = "review", fetch = FetchType.LAZY)
    private Event event;

    public Review(Feedback feedback, String imageFile) {
        this.feedback = feedback;
        this.imageFile = imageFile;
    }
    public void updateFeedback(Feedback feedback) {
        if (feedback == null) {
         throw new BusinessException(ReviewErrorCode.REVIEW_UPDATE_FAILED);
        }
        this.feedback = feedback;
    }
    public void updateImageFile(String imageFile) {
        this.imageFile = imageFile;
    }
}
