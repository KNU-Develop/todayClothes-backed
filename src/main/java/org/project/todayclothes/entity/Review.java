package org.project.todayclothes.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.project.todayclothes.exception.BusinessException;
import org.project.todayclothes.exception.code.ReviewErrorCode;
import org.project.todayclothes.global.Feedback;

import java.util.ArrayList;
import java.util.List;


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

    @ElementCollection
    @CollectionTable(name = "review_images", joinColumns = @JoinColumn(name = "review_id"))
    @Column(name = "image_path")
    private List<String> imageFiles = new ArrayList<>();

    @OneToOne(mappedBy = "review", fetch = FetchType.LAZY)
    private Event event;

    public Review(Feedback feedback, List<String> imageFiles) {
        if (feedback == null) {
            throw new BusinessException(ReviewErrorCode.INVALID_FEEDBACK_ENUM);
        }
        if (imageFiles.size() > 4) {
            throw new BusinessException(ReviewErrorCode.FILE_SIZE_EXCEEDED);
        }
        this.feedback = feedback;
        this.imageFiles = imageFiles;
    }
    public void updateFeedback(Feedback feedback) {
        if (feedback == null) {
         throw new BusinessException(ReviewErrorCode.REVIEW_UPDATE_FAILED);
        }
        this.feedback = feedback;
    }
    public void addImageFile(String imageFile) {
        if (this.imageFiles.size() >= 4) {
            throw new BusinessException(ReviewErrorCode.FILE_SIZE_EXCEEDED);
        }
        this.imageFiles.add(imageFile);
    }

    public void removeImageFile(String imageFile) {
        this.imageFiles.remove(imageFile);
    }
}
