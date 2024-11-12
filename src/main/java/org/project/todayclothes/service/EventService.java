package org.project.todayclothes.service;

import lombok.AllArgsConstructor;
import org.project.todayclothes.dto.ClothesResDto;
import org.project.todayclothes.dto.EventReqDto;
import org.project.todayclothes.dto.EventResDto;
import org.project.todayclothes.dto.ReqRecommendClotheDto;
import org.project.todayclothes.entity.Event;
import org.project.todayclothes.entity.User;
import org.project.todayclothes.entity.Weather;
import org.project.todayclothes.exception.BusinessException;
import org.project.todayclothes.exception.code.EventErrorCode;
import org.project.todayclothes.exception.code.UserErrorCode;
import org.project.todayclothes.global.Feedback;
import org.project.todayclothes.repository.EventRepository;
import org.project.todayclothes.repository.ReviewRepository;
import org.project.todayclothes.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final ClothesService clothesService;

    private static final double GAMMA = 1.0;
    private static final double FEEDBACK_UNIT_CHANGE = 0.5;

    private User findUserById(String socialId) {
        return userRepository.findBySocialId(socialId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public List<ClothesResDto> getUserClothesRecords(String socialId) {
        User user = findUserById(socialId);
        List<Event> events = eventRepository.findAllByUserSocialId(socialId);
        return events.stream()
                .map(ClothesResDto::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public EventResDto createEvent(String socialId, EventReqDto eventReqDto) throws IOException {
        User user = findUserById(socialId);
        if (eventReqDto.getWeather() == null) {
            throw new BusinessException(EventErrorCode.INVALID_EVENT_DETAILS);
        }
        Weather weather = new Weather(eventReqDto);
        Event event = new Event(eventReqDto, weather, user);
        Event savedEvent = eventRepository.save(event);
        ReqRecommendClotheDto recommendClotheDto = eventReqDto.toRecommendClotheDto();
        String recommendedImageUrl = clothesService.recommendClothe(recommendClotheDto).getRecommendClotheUrl();
        savedEvent.updateImagePath(recommendedImageUrl);
        savedEvent.updateImagePath(recommendedImageUrl);
        return convertToEventResDto(savedEvent);
    }


    @Transactional
    public void updateEvent(String socialId, Long eventId, EventReqDto eventReqDto) {
        User user = findUserById(socialId);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new BusinessException(EventErrorCode.EVENT_NOT_FOUND));
        event.updateWeather(eventReqDto);
        event.updateEvent(eventReqDto);
    }

    @Transactional(readOnly = true)
    public List<ClothesResDto> getAllEvents(int page, int size, String socialId, Long userId) {
        User user = findUserById(socialId);
        PageRequest pageRequest = PageRequest.of(page, size, Sort.Direction.DESC,"createdAt");
        Page<Event> eventPage;
        if (userId != null) {
            eventPage = eventRepository.findAllByUserId(userId, pageRequest);
        } else {
            eventPage = eventRepository.findAll(pageRequest);
        }
        return eventPage.stream()
                .map(ClothesResDto::from)
                .collect(Collectors.toList());
        }

    private EventResDto convertToEventResDto(Event event) {
        Weather weather = event.getWeather();
        return EventResDto.builder()
                .location(event.getLocation())
                .imgPath(event.getImagePath())
                .comment(event.getComment())
                .type(event.getType())
                .style(event.getStyle())
                .gender(event.getGender())
                .timezone(event.getTimezone())
                .weather(weather.getWeather())
                .wind(weather.getWind())
                .rain(weather.getRain())
                .humidity(weather.getHumidity())
                .feelsLike(weather.getFeelsLike())
                .temp(weather.getTemp())
                .build();
    }
}

