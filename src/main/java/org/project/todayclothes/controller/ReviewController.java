package org.project.todayclothes.controller;

import lombok.RequiredArgsConstructor;
import org.project.todayclothes.entity.Review;
import org.project.todayclothes.global.Feedback;
import org.project.todayclothes.service.ReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<Review> createReview(
            @RequestParam Feedback feedback,
            @RequestParam("file") MultipartFile imageFile) {

        Review review = reviewService.createReview(feedback, imageFile);
        return ResponseEntity.ok(review);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Review> getReview(@PathVariable Long id) {
        Review review = reviewService.getReviewById(id);
        return ResponseEntity.ok(review);
    }

    @GetMapping
    public ResponseEntity<List<Review>> getAllReviews() {
        List<Review> reviews = reviewService.getAllReviews();
        return ResponseEntity.ok(reviews);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Review> updateReview(
            @PathVariable Long id,
            @RequestParam Feedback feedback,
            @RequestParam(value = "file", required = false) MultipartFile imageFile) {

        Review updatedReview = reviewService.updateReview(id, feedback, imageFile);
        return ResponseEntity.ok(updatedReview);
    }
}
