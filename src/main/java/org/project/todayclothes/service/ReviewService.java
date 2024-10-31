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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    public Review createReview(String socialId, ReviewReq reviewReq, List<MultipartFile> imageFiles) {
        User user = findUserById(socialId);
        Event event = eventRepository.findById(reviewReq.getClothesId())
                .orElseThrow(() -> new BusinessException(EventErrorCode.EVENT_NOT_FOUND));
        if (event.getReview() != null) {
            throw new BusinessException(ReviewErrorCode.REVIEW_ALREADY_EXISTS);
        }
        if (!Feedback.isValid(String.valueOf(reviewReq.getFeedback()))) {
            throw new BusinessException(ReviewErrorCode.INVALID_FEEDBACK_ENUM);
        }
        List<String> imageUrls = processImageFiles(imageFiles);
        if (!imageUrls.isEmpty()) {
            event.updateImagePath(imageUrls);
            eventRepository.save(event);
        }
        Review review = new Review(reviewReq.getFeedback(), imageUrls);
        reviewRepository.save(review);

        event.associateReview(review);
        eventRepository.save(event);

        return review;
    }

    @Transactional
    public Review updateReview(String socialId, Long reviewId, ReviewReq reviewReq, List<MultipartFile> imageFiles) {
        User user = findUserById(socialId);
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BusinessException(ReviewErrorCode.REVIEW_NOT_FOUND));

        if (!Feedback.isValid(String.valueOf(reviewReq.getFeedback()))) {
            throw new BusinessException(ReviewErrorCode.INVALID_FEEDBACK_ENUM);
        }
        review.updateFeedback(reviewReq.getFeedback());

        if (imageFiles != null && !imageFiles.isEmpty()) {
            review.getImageFiles().forEach(s3UploadService::deletePhoto);

            List<String> imageUrls = processImageFiles(imageFiles);
            //review.setImageFiles(imageUrls);
        }

        return reviewRepository.save(review);
    }

    private List<String> processImageFiles(List<MultipartFile> imageFiles) {
        if (imageFiles == null || imageFiles.isEmpty()) return new ArrayList<>();

        if (imageFiles.size() > 4) {
            throw new BusinessException(ReviewErrorCode.FILE_SIZE_EXCEEDED);
        }

        return imageFiles.stream()
                .map(s3UploadService::savePhoto)
                .collect(Collectors.toList());
    }

}
