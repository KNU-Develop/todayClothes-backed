package org.project.todayclothes.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.project.todayclothes.dto.ReviewReq;
import org.project.todayclothes.entity.Review;
import org.project.todayclothes.entity.User;
import org.project.todayclothes.exception.BusinessException;
import org.project.todayclothes.exception.code.ReviewErrorCode;
import org.project.todayclothes.exception.code.UserErrorCode;
import org.project.todayclothes.global.Feedback;
import org.project.todayclothes.repository.ReviewRepository;
import org.project.todayclothes.repository.UserRepository;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ReviewServiceTest {

    @InjectMocks
    private ReviewService reviewService;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private S3UploadService s3UploadService;

    @Mock
    private MultipartFile multipartFile;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // 1. 정상적으로 리뷰가 생성되는 경우
    @Test
    void createReview_Success() {

        Long userId = 1L;
        ReviewReq reviewReq = new ReviewReq(Feedback.PERFECT);
        User user = new User("userId", "email@example.com", "USER_ROLE");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(s3UploadService.savePhoto(any(MultipartFile.class))).thenReturn("image-url");

        Review savedReview = new Review(Feedback.PERFECT, "image-url");
        when(reviewRepository.save(any(Review.class))).thenReturn(savedReview);

        Review result = reviewService.createReview(userId, reviewReq, multipartFile);

        assertNotNull(result);
        assertEquals(Feedback.PERFECT, result.getFeedback());
        assertEquals("image-url", result.getImageFile());
        verify(userRepository).findById(userId);
        verify(reviewRepository).save(any(Review.class));
        verify(s3UploadService).savePhoto(any(MultipartFile.class));
    }

    // 2. 유저가 없을 때 예외 발생
    @Test
    void createReview_UserNotFound_Exception() {
        Long userId = 1L;
        ReviewReq reviewReq = new ReviewReq(Feedback.PERFECT);

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            reviewService.createReview(userId, reviewReq, null);
        });

        assertEquals(UserErrorCode.USER_NOT_FOUND, exception.getErrorCode());
        verify(userRepository).findById(userId);
    }

    // 3. 잘못된 Feedback Enum이 들어왔을 때 예외 발생
    @Test
    void createReview_InvalidFeedback_Exception() {
        Long userId = 1L;
        ReviewReq reviewReq = new ReviewReq(null); // 잘못된 피드백 (null)
        User user = new User("userId", "email@example.com", "USER_ROLE");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            reviewService.createReview(userId, reviewReq, null);
        });

        assertEquals(ReviewErrorCode.INVALID_FEEDBACK_ENUM, exception.getErrorCode());
        verify(userRepository).findById(userId);
    }

    // 4. 정상적으로 리뷰가 업데이트되는 경우
    @Test
    void updateReview_Success() {

        Long userId = 1L;
        Long reviewId = 1L;
        ReviewReq reviewReq = new ReviewReq(Feedback.COLD);
        User user = new User("socialId", "email@example.com", "USER_ROLE");
        Review existingReview = new Review(Feedback.PERFECT, "old-image-url");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(existingReview));
        when(s3UploadService.savePhoto(any(MultipartFile.class))).thenReturn("new-image-url");

        reviewService.updateReview(userId, reviewId, reviewReq, multipartFile);

        assertEquals(Feedback.COLD, existingReview.getFeedback());
        assertEquals("new-image-url", existingReview.getImageFile());
        verify(userRepository).findById(userId);
        verify(reviewRepository).findById(reviewId);
        verify(s3UploadService).savePhoto(any(MultipartFile.class));
    }

    // 5. 리뷰가 없을 때 예외 발생
    @Test
    void updateReview_ReviewNotFound_Exception() {

        Long userId = 1L;
        Long reviewId = 1L;
        ReviewReq reviewReq = new ReviewReq(Feedback.PERFECT);

        when(userRepository.findById(userId)).thenReturn(Optional.of(new User("userId", "email@example.com", "USER_ROLE")));
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            reviewService.updateReview(userId, reviewId, reviewReq, null);
        });

        assertEquals(ReviewErrorCode.REVIEW_NOT_FOUND, exception.getErrorCode());
        verify(userRepository).findById(userId);
        verify(reviewRepository).findById(reviewId);
    }
}
