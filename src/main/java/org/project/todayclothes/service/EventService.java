package org.project.todayclothes.service;

import lombok.AllArgsConstructor;
import org.project.todayclothes.dto.ClothesResDto;
import org.project.todayclothes.dto.EventReqDto;
import org.project.todayclothes.dto.EventResDto;
import org.project.todayclothes.entity.Event;
import org.project.todayclothes.entity.User;
import org.project.todayclothes.entity.Weather;
import org.project.todayclothes.exception.BusinessException;
import org.project.todayclothes.exception.code.EventErrorCode;
import org.project.todayclothes.exception.code.UserErrorCode;
import org.project.todayclothes.repository.EventRepository;
import org.project.todayclothes.repository.UserRepository;
import org.project.todayclothes.repository.WeatherRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public List<ClothesResDto> getUserClothesRecords(Long userId) {
        User user = findUserById(userId);
        List<Event> events = eventRepository.findAllByUserId(userId);
        return events.stream()
                .map(ClothesResDto::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public EventResDto createEvent(Long userId, EventReqDto eventReqDto) {
        User user = findUserById(userId);
        if (eventReqDto.getWeather() == null) {
            throw new BusinessException(EventErrorCode.INVALID_EVENT_DETAILS);
        }
        Weather weather = new Weather(eventReqDto);
        Event event = new Event(eventReqDto, weather);
        String comment = generateClothesComment(eventReqDto.getFeelsLike());
        Event savedEvent = eventRepository.save(event);
        return EventResDto.builder()
                .location(event.getLocation())
                .imgPath(event.getImagePath())
                .comment(comment)
                .type(event.getType())
                .style(event.getStyle())
                .weather(eventReqDto.getWeather())
                .wind(eventReqDto.getWind())
                .rain(eventReqDto.getRain())
                .humidity(eventReqDto.getHumidity())
                .feelsLike(eventReqDto.getFeelsLike())
                .temp(eventReqDto.getTemp())
                .build();
    }


    @Transactional
    public void updateEvent(Long userId, Long eventId, EventReqDto eventReqDto) {
        User user = findUserById(userId);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new BusinessException(EventErrorCode.EVENT_NOT_FOUND));

        event.updateWeather(eventReqDto);
        event.updateEvent(eventReqDto);
    }

    private String generateClothesComment(double feelsLike) {
        if (feelsLike <= 10) {
            return "두꺼운 옷을 추천합니다.";
        } else if (feelsLike <= 20) {
            return "가벼운 재킷을 추천합니다.";
        } else {
            return "가벼운 옷을 입으셔도 좋습니다.";
        }
    }
}

