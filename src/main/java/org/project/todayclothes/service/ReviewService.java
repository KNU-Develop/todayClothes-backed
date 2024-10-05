package org.project.todayclothes.service;

import lombok.RequiredArgsConstructor;
import org.project.todayclothes.dto.ReviewReq;
import org.project.todayclothes.entity.Review;
import org.project.todayclothes.exception.CustomException;
import org.project.todayclothes.exception.code.ReviewErrorCode;
import org.project.todayclothes.exception.code.UserErrorCode;
import org.project.todayclothes.repository.ReviewRepository;
import org.project.todayclothes.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final S3UploadService s3UploadService;

    @Transactional
    public Review createReview(Long userId, ReviewReq reviewReq, MultipartFile imageFile) {
        userRepository.findById(userId).orElseThrow(()-> new CustomException(UserErrorCode.USER_NOT_FOUND));
        String imageUrl = s3UploadService.savePhoto(imageFile);
        Review review = new Review(reviewReq.getFeedback(), imageUrl);
        return reviewRepository.save(review);
    }

    @Transactional(readOnly = true)
    public Review getReviewById(Long userId, Long reviewId) {
        userRepository.findById(userId).orElseThrow(()-> new CustomException(UserErrorCode.USER_NOT_FOUND));
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new CustomException(ReviewErrorCode.REVIEW_NOT_FOUND));
    }

    @Transactional
    public Review updateReview(Long userId, Long reviewId, ReviewReq reviewReq, MultipartFile imageFile) {
        userRepository.findById(userId).orElseThrow(()-> new CustomException(UserErrorCode.USER_NOT_FOUND));
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new CustomException(ReviewErrorCode.REVIEW_NOT_FOUND));

        review.updateFeedback(reviewReq.getFeedback());

        if (imageFile != null && !imageFile.isEmpty()) {
            s3UploadService.deletePhoto(review.getImageFile());
            String imageUrl = s3UploadService.savePhoto(imageFile);
            review.updateImageFile(imageUrl);
        }
        return reviewRepository.save(review);
    }

}
