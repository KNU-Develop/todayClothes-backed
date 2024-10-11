package org.project.todayclothes.controller;

import lombok.RequiredArgsConstructor;
import org.project.todayclothes.dto.ClothesResDto;
import org.project.todayclothes.dto.EventReqDto;
import org.project.todayclothes.dto.EventResDto;
import org.project.todayclothes.dto.oauth2.CustomOAuth2User;
import org.project.todayclothes.exception.Api_Response;
import org.project.todayclothes.exception.code.SuccessCode;
import org.project.todayclothes.service.EventService;
import org.project.todayclothes.util.ApiResponseUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/event")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    @PostMapping
    public ResponseEntity<Api_Response<EventResDto>> createEvent(
            @AuthenticationPrincipal CustomOAuth2User customOAuth2User,
            @RequestBody EventReqDto eventReqDto) {
        Long userId = customOAuth2User.getUserId();
        EventResDto eventResDto = eventService.createEvent(userId, eventReqDto);
        return ApiResponseUtil.createSuccessResponse(SuccessCode.INSERT_SUCCESS.getCode(), eventResDto);
    }

    @GetMapping
    public ResponseEntity<Api_Response<List<ClothesResDto>>> getAllEvents(
            @AuthenticationPrincipal CustomOAuth2User customOAuth2User) {
        Long userId = customOAuth2User.getUserId();
        List<ClothesResDto> records = eventService.getUserClothesRecords(userId);
        return ApiResponseUtil.createSuccessResponse(SuccessCode.SELECT_SUCCESS.getCode(), records);
    }

    @PutMapping("/{eventId}")
    public ResponseEntity<Api_Response<EventResDto>> updateEvent(
            @AuthenticationPrincipal CustomOAuth2User customOAuth2User,
            @PathVariable Long eventId,
            @RequestBody EventReqDto eventReqDto) {
        Long userId = customOAuth2User.getUserId();
        eventService.updateEvent(userId, eventId, eventReqDto);
        return ApiResponseUtil.createSuccessResponse(SuccessCode.UPDATE_SUCCESS.getCode());
    }
}
