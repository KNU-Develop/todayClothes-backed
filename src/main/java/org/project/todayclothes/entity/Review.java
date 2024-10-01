package org.project.todayclothes.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.project.todayclothes.global.Feedback;


@Entity
@Getter
@NoArgsConstructor
public class Review {
    @Id
    private Long id;
    @Enumerated(EnumType.STRING)
    private Feedback feedback;
    private String imageFile;

    public Review(Long id, Feedback feedback, String imageFile) {
        this.id = id;
        this.feedback = feedback;
        this.imageFile = imageFile;
    }
    public void updateFeedback(Feedback feedback) {
        this.feedback = feedback;
    }
    public void updateImageFile(String imageFile) {
        this.imageFile = imageFile;
    }
}
