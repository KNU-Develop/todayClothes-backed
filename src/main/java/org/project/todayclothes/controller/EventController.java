package org.project.todayclothes.controller;

import lombok.RequiredArgsConstructor;
import org.project.todayclothes.dto.ClothesResDto;
import org.project.todayclothes.dto.EventReqDto;
import org.project.todayclothes.dto.EventResDto;
import org.project.todayclothes.dto.oauth2.CustomOAuth2User;
import org.project.todayclothes.exception.Api_Response;
import org.project.todayclothes.exception.code.CommonErrorCode;
import org.project.todayclothes.exception.code.SuccessCode;
import org.project.todayclothes.service.EventService;
import org.project.todayclothes.utils.ApiResponseUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/event")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    @PostMapping
    public ResponseEntity<Api_Response<EventResDto>> createEvent(
            @AuthenticationPrincipal CustomOAuth2User customOAuth2User,
            @RequestBody EventReqDto eventReqDto) throws IOException {
        String socialId = customOAuth2User.getSocialId();
        if (socialId == null || socialId.isEmpty()) {
            return ApiResponseUtil.createErrorResponse(CommonErrorCode.UNAUTHORIZED_MEMBER);
        }
        EventResDto eventResDto = eventService.createEvent(socialId, eventReqDto);
        return ApiResponseUtil.createSuccessResponse(SuccessCode.INSERT_SUCCESS.getCode(), eventResDto);
    }

    @GetMapping
    public ResponseEntity<Api_Response<List<ClothesResDto>>> getMyEvents(
            @AuthenticationPrincipal CustomOAuth2User customOAuth2User) {
        String socialId = customOAuth2User.getSocialId();
        if (socialId == null || socialId.isEmpty()) {
            return ApiResponseUtil.createErrorResponse(CommonErrorCode.UNAUTHORIZED_MEMBER);
        }
        List<ClothesResDto> records = eventService.getUserClothesRecords(socialId);
        return ApiResponseUtil.createSuccessResponse(SuccessCode.SELECT_SUCCESS.getCode(), records);
    }

    @GetMapping("/all")
    public ResponseEntity<Api_Response<List<ClothesResDto>>> getAllEvents(
        @AuthenticationPrincipal CustomOAuth2User customOAuth2User,
        @RequestParam int page, @RequestParam int size,
        @RequestParam(required = false) Long userId) {
    String socialId = customOAuth2User.getSocialId();
    if (socialId == null || socialId.isEmpty()) {
        return ApiResponseUtil.createErrorResponse(CommonErrorCode.UNAUTHORIZED_MEMBER);
    }
    List<ClothesResDto> events = eventService.getAllEvents(page, size, socialId, userId);
    return ApiResponseUtil.createSuccessResponse(SuccessCode.SELECT_SUCCESS.getCode(), events);
}

    @PutMapping("/{eventId}")
    public ResponseEntity<Api_Response<EventResDto>> updateEvent(
            @AuthenticationPrincipal CustomOAuth2User customOAuth2User,
            @PathVariable Long eventId,
            @RequestBody EventReqDto eventReqDto) {
        String socialId = customOAuth2User.getSocialId();
        eventService.updateEvent(socialId, eventId, eventReqDto);
        return ApiResponseUtil.createSuccessResponse(SuccessCode.UPDATE_SUCCESS.getCode());
    }
}
