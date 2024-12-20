package org.project.todayclothes.controller;

import lombok.RequiredArgsConstructor;
import org.project.todayclothes.dto.ReviewReq;
import org.project.todayclothes.dto.oauth2.CustomOAuth2User;
import org.project.todayclothes.exception.Api_Response;
import org.project.todayclothes.exception.code.CommonErrorCode;
import org.project.todayclothes.exception.code.SuccessCode;
import org.project.todayclothes.service.ReviewService;
import org.project.todayclothes.utils.ApiResponseUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/clothes/review")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<Api_Response<ReviewReq>> createReview(
            @AuthenticationPrincipal CustomOAuth2User customOAuth2User,
            @RequestPart(value = "reviewReq") ReviewReq reviewReq,
            @RequestPart(value = "imageFiles", required = false) List<MultipartFile> imageFiles) {
        String socialId = customOAuth2User.getSocialId();
        if (socialId == null || socialId.isEmpty()) {
            return ApiResponseUtil.createErrorResponse(CommonErrorCode.UNAUTHORIZED_MEMBER);
        }
        reviewService.createReview(socialId,reviewReq, imageFiles);
        return ApiResponseUtil.createSuccessResponse(SuccessCode.INSERT_SUCCESS.getMessage());
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<Api_Response<ReviewReq>> updateReview(
            @AuthenticationPrincipal CustomOAuth2User customOAuth2User,
            @PathVariable Long reviewId,
            @RequestPart(value = "reviewReq") ReviewReq reviewReq,
            @RequestPart(value = "imageFiles", required = false) List<MultipartFile> imageFiles) {
        String socialId = customOAuth2User.getSocialId();
        if (socialId == null || socialId.isEmpty()) {
            return ApiResponseUtil.createErrorResponse(CommonErrorCode.UNAUTHORIZED_MEMBER);
        }
        reviewService.updateReview(socialId, reviewId, reviewReq, imageFiles);
        return ApiResponseUtil.createSuccessResponse(SuccessCode.UPDATE_SUCCESS.getCode());
    }
}
