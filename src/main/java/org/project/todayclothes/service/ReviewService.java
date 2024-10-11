package org.project.todayclothes.service;

import lombok.RequiredArgsConstructor;
import org.project.todayclothes.dto.ReviewReq;
import org.project.todayclothes.entity.Review;
import org.project.todayclothes.exception.BusinessException;
import org.project.todayclothes.exception.code.ReviewErrorCode;
import org.project.todayclothes.exception.code.UserErrorCode;
import org.project.todayclothes.global.Feedback;
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
        String imageUrl = null;
        userRepository.findById(userId).orElseThrow(()-> new BusinessException(UserErrorCode.USER_NOT_FOUND));
        if (!Feedback.isValid(String.valueOf(reviewReq.getFeedback()))) {
            throw new BusinessException(ReviewErrorCode.INVALID_FEEDBACK_ENUM);
        }
        if (imageFile != null && !imageFile.isEmpty()) {
            imageUrl = s3UploadService.savePhoto(imageFile);
        }
        Review review = new Review(reviewReq.getFeedback(), imageUrl);
        return reviewRepository.save(review);
    }

    @Transactional
    public Review updateReview(Long userId, Long reviewId, ReviewReq reviewReq, MultipartFile imageFile) {
        userRepository.findById(userId).orElseThrow(()-> new BusinessException(UserErrorCode.USER_NOT_FOUND));
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BusinessException(ReviewErrorCode.REVIEW_NOT_FOUND));
        if (!Feedback.isValid(String.valueOf(reviewReq.getFeedback()))) {
            throw new BusinessException(ReviewErrorCode.INVALID_FEEDBACK_ENUM);
        }
        review.updateFeedback(reviewReq.getFeedback());

        if (imageFile != null && !imageFile.isEmpty()) {
            s3UploadService.deletePhoto(review.getImageFile());
            String imageUrl = s3UploadService.savePhoto(imageFile);
            review.updateImageFile(imageUrl);
        }
        return reviewRepository.save(review);
    }

}
