package org.project.todayclothes.service;

import lombok.RequiredArgsConstructor;
import org.project.todayclothes.entity.Review;
import org.project.todayclothes.exception.CustomException;
import org.project.todayclothes.exception.code.ReviewErrorCode;
import org.project.todayclothes.global.Feedback;
import org.project.todayclothes.repository.ReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final S3UploadService s3UploadService; // S3 업로드 서비스

    @Transactional
    public Review createReview(Feedback feedback, MultipartFile imageFile) {
        String imageUrl = s3UploadService.savePhoto(imageFile);

        Review review = new Review(null, feedback, imageUrl);
        return reviewRepository.save(review);
    }

    @Transactional(readOnly = true)
    public Review getReviewById(Long id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new CustomException(ReviewErrorCode.REVIEW_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    @Transactional
    public Review updateReview(Long id, Feedback feedback, MultipartFile imageFile) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new CustomException(ReviewErrorCode.REVIEW_NOT_FOUND));

        review.updateFeedback(feedback);

        if (imageFile != null && !imageFile.isEmpty()) {
            String imageUrl = s3UploadService.savePhoto(imageFile);
            review.updateImageFile(imageUrl);
        }

        return reviewRepository.save(review);
    }

}
