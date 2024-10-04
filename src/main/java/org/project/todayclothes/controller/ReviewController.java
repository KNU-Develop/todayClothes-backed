package org.project.todayclothes.controller;

import lombok.RequiredArgsConstructor;
import org.project.todayclothes.dto.ReviewReq;
import org.project.todayclothes.exception.Api_Response;
import org.project.todayclothes.exception.code.SuccessCode;
import org.project.todayclothes.service.ReviewService;
import org.project.todayclothes.util.ApiResponseUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/review")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<Api_Response<ReviewReq>> createReview(
            @AuthenticationPrincipal Long userId,
            @RequestBody ReviewReq reviewReq,
            @RequestBody MultipartFile imageFile) {
        reviewService.createReview(userId,reviewReq, imageFile);
        return ApiResponseUtil.createSuccessResponse(SuccessCode.INSERT_SUCCESS.getCode());
    }

    @GetMapping("/{reviewId}")
    public ResponseEntity<Api_Response<ReviewReq>> getReview(@AuthenticationPrincipal Long userId, @PathVariable Long reviewId) {
        reviewService.getReviewById(userId,reviewId);
        return ApiResponseUtil.createSuccessResponse(SuccessCode.SELECT_SUCCESS.getCode());
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<Api_Response<ReviewReq>> updateReview(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long reviewId,
            @RequestBody ReviewReq reviewReq,
            @RequestBody MultipartFile imageFile) {
        reviewService.updateReview(userId, reviewId, reviewReq, imageFile);
        return ApiResponseUtil.createSuccessResponse(SuccessCode.UPDATE_SUCCESS.getCode());
    }
}
