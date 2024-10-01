package org.project.todayclothes;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.project.todayclothes.entity.Review;
import org.project.todayclothes.global.Feedback;
import org.project.todayclothes.repository.ReviewRepository;
import org.project.todayclothes.service.ReviewService;
import org.project.todayclothes.service.S3UploadService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReviewServiceTest {

    @InjectMocks
    private ReviewService reviewService;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private S3UploadService s3UploadService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createReview() {

        String imageUrl = "https://s3.amazonaws.com/test-image.jpg";
        Feedback feedback = Feedback.PERFECT;
        when(s3UploadService.savePhoto(any())).thenReturn(imageUrl);

        Review review = new Review(1L, feedback, imageUrl);
        when(reviewRepository.save(any(Review.class))).thenReturn(review);

        Review result = reviewService.createReview(feedback, null);

        assertNotNull(result);
        assertEquals(feedback, result.getFeedback());
        assertEquals(imageUrl, result.getImageFile());
        verify(s3UploadService, times(1)).savePhoto(any());
        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @Test
    void getReviewById() {
        Long reviewId = 1L;
        Feedback feedback = Feedback.PERFECT;
        String imageUrl = "https://s3.amazonaws.com/test-image.jpg";
        Review review = new Review(reviewId, feedback, imageUrl);
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

        Review result = reviewService.getReviewById(reviewId);


        assertNotNull(result);
        assertEquals(feedback, result.getFeedback());
        assertEquals(imageUrl, result.getImageFile());
        verify(reviewRepository, times(1)).findById(reviewId);
    }

    @Test
    void updateReview() {

        Long reviewId = 1L;
        Feedback oldFeedback = Feedback.HOT;
        Feedback newFeedback = Feedback.PERFECT;
        String oldImageUrl = "https://s3.amazonaws.com/old-image.jpg";
        String newImageUrl = "https://s3.amazonaws.com/new-image.jpg";
        Review review = new Review(reviewId, oldFeedback, oldImageUrl);

        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
        when(s3UploadService.savePhoto(any())).thenReturn(newImageUrl);
        when(reviewRepository.save(any(Review.class))).thenReturn(review);

        Review updatedReview = reviewService.updateReview(reviewId, newFeedback, null);

        assertNotNull(updatedReview);
        assertEquals(newFeedback, updatedReview.getFeedback());
        assertEquals(oldImageUrl, updatedReview.getImageFile()); // 이미지 파일이 null이므로 기존 URL 유지
        verify(reviewRepository, times(1)).findById(reviewId);
        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @Test
    void updateReviewWithImage() {
        Long reviewId = 1L;
        Feedback feedback = Feedback.PERFECT;
        String oldImageUrl = "https://s3.amazonaws.com/old-image.jpg";
        String newImageUrl = "https://s3.amazonaws.com/new-image.jpg";
        Review review = new Review(reviewId, feedback, oldImageUrl);

        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
        when(s3UploadService.savePhoto(any())).thenReturn(newImageUrl);
        when(reviewRepository.save(any(Review.class))).thenReturn(review);

        Review updatedReview = reviewService.updateReview(reviewId, feedback, null);

        assertNotNull(updatedReview);
        assertEquals(feedback, updatedReview.getFeedback());
        assertEquals(oldImageUrl, updatedReview.getImageFile());
        verify(reviewRepository, times(1)).findById(reviewId);
        verify(reviewRepository, times(1)).save(any(Review.class));
    }
}
