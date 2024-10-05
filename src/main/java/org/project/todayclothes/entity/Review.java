package org.project.todayclothes.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
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

    public Review(Feedback feedback, String imageFile) {
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
