package org.project.todayclothes.service;

import lombok.AllArgsConstructor;
import org.project.todayclothes.dto.EventDto;
import org.project.todayclothes.dto.EventResDto;
import org.project.todayclothes.entity.Event;
import org.project.todayclothes.entity.User;
import org.project.todayclothes.entity.Weather;
import org.project.todayclothes.exception.CustomException;
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
    private final WeatherRepository weatherRepository;

    @Transactional(readOnly = true)
    public List<EventResDto> getAllEvents(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));
        return eventRepository.findAll().stream()
                .map(this::convertToResDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EventResDto getEventById(Long userId, Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new CustomException(EventErrorCode.EVENT_NOT_FOUND));
        return convertToResDto(event);
    }

    @Transactional
    public EventResDto createEvent(Long userId, EventDto eventDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));
        Weather weather = new Weather(eventDto);
        Event event = new Event(eventDto, weather);

        Event savedEvent = eventRepository.save(event);
        return convertToResDto(savedEvent);
    }


    @Transactional
    public void updateEvent(Long userId, Long eventId, EventDto eventDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new CustomException(EventErrorCode.EVENT_NOT_FOUND));
        event.updateEvent(eventDto);
        event.getWeather().updateWeather(eventDto);
        eventRepository.save(event);
    }

    private EventResDto convertToResDto(Event event) {
        return new EventResDto(event);
    }
}

