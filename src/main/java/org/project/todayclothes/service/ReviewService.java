package org.project.todayclothes.service;

import lombok.RequiredArgsConstructor;
import org.project.todayclothes.dto.ReviewReq;
import org.project.todayclothes.entity.Event;
import org.project.todayclothes.entity.Review;
import org.project.todayclothes.entity.User;
import org.project.todayclothes.exception.BusinessException;
import org.project.todayclothes.exception.code.EventErrorCode;
import org.project.todayclothes.exception.code.ReviewErrorCode;
import org.project.todayclothes.exception.code.UserErrorCode;
import org.project.todayclothes.global.Feedback;
import org.project.todayclothes.repository.EventRepository;
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
    private final EventRepository eventRepository;
    private final S3UploadService s3UploadService;

    private User findUserById(String socialId) {
        return userRepository.findBySocialId(socialId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));
    }

    @Transactional
    public Review createReview(String socialId, ReviewReq reviewReq, MultipartFile imageFile) {
        User user = findUserById(socialId);
        Event event = eventRepository.findById(reviewReq.getClothesId())
                .orElseThrow(() -> new BusinessException(EventErrorCode.EVENT_NOT_FOUND));
        if (event.getReview() != null) {
            throw new BusinessException(ReviewErrorCode.REVIEW_ALREADY_EXISTS);
        }
        if (!Feedback.isValid(String.valueOf(reviewReq.getFeedback()))) {
            throw new BusinessException(ReviewErrorCode.INVALID_FEEDBACK_ENUM);
        }
        String imageUrl = processImageFile(imageFile);
        if (imageUrl != null) {
            event.changeImagePath(imageUrl);
        }
        Review review = new Review(reviewReq.getFeedback(), imageUrl);
        reviewRepository.save(review);

        event.associateReview(review);
        eventRepository.save(event);

        return review;
    }

    @Transactional
    public Review updateReview(String socialId, Long reviewId, ReviewReq reviewReq, MultipartFile imageFile) {
        User user = findUserById(socialId);
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BusinessException(ReviewErrorCode.REVIEW_NOT_FOUND));

        if (!Feedback.isValid(String.valueOf(reviewReq.getFeedback()))) {
            throw new BusinessException(ReviewErrorCode.INVALID_FEEDBACK_ENUM);
        }
        review.updateFeedback(reviewReq.getFeedback());

        if (imageFile != null && !imageFile.isEmpty()) {
            s3UploadService.deletePhoto(review.getImageFile());
            String imageUrl = processImageFile(imageFile);
            review.updateImageFile(imageUrl);
        }
        return reviewRepository.save(review);
    }

    private String processImageFile(MultipartFile imageFile) {
        if (imageFile != null && !imageFile.isEmpty()) {
            return s3UploadService.savePhoto(imageFile);
        }
        return null;
    }

}
