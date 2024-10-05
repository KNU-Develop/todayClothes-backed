package org.project.todayclothes.controller;

import lombok.RequiredArgsConstructor;
import org.project.todayclothes.dto.EventDto;
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
            @RequestBody EventDto eventDto) {
        Long userId = customOAuth2User.getUserId();
        EventResDto eventResDto = eventService.createEvent(userId, eventDto);
        return ApiResponseUtil.createSuccessResponse(SuccessCode.INSERT_SUCCESS.getCode(), eventResDto);
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<Api_Response<EventResDto>> getEvent(
            @AuthenticationPrincipal CustomOAuth2User customOAuth2User,
            @PathVariable Long eventId) {
        Long userId = customOAuth2User.getUserId();
        EventResDto eventResDto = eventService.getEventById(userId, eventId);
        return ApiResponseUtil.createSuccessResponse(SuccessCode.SELECT_SUCCESS.getCode(), eventResDto);
    }

    @GetMapping
    public ResponseEntity<Api_Response<List<EventResDto>>> getAllEvents(@AuthenticationPrincipal CustomOAuth2User customOAuth2User) {
        Long userId = customOAuth2User.getUserId();
        List<EventResDto> events = eventService.getAllEvents(userId);
        return ApiResponseUtil.createSuccessResponse(SuccessCode.SELECT_SUCCESS.getCode(), events);
    }

    @PutMapping("/{eventId}")
    public ResponseEntity<Api_Response<EventResDto>> updateEvent(
            @AuthenticationPrincipal CustomOAuth2User customOAuth2User,
            @PathVariable Long eventId,
            @RequestBody EventDto eventDto) {
        Long userId = customOAuth2User.getUserId();
        eventService.updateEvent(userId, eventId, eventDto);
        return ApiResponseUtil.createSuccessResponse(SuccessCode.UPDATE_SUCCESS.getCode());
    }
}
