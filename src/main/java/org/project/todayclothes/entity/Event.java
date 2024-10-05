package org.project.todayclothes.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.project.todayclothes.dto.EventDto;
import org.project.todayclothes.exception.CustomException;
import org.project.todayclothes.exception.code.EventErrorCode;
import org.project.todayclothes.global.Style;
import org.project.todayclothes.global.Type;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String location;
    @Enumerated(EnumType.STRING)
    private Type type;
    @Enumerated(EnumType.STRING)
    private Style style;
    private String imagePath;
    private String comment;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private Review review;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "weather_id")
    private Weather weather;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public Event(EventDto eventDto, Weather weather) {
        this.startTime = eventDto.getStartTime();
        this.endTime = eventDto.getEndTime();
        this.location = eventDto.getLocation();
        this.type = eventDto.getType();
        this.style = eventDto.getStyle();
        this.weather = weather;
    }

    public void updateEvent(EventDto eventDto) {
        if (eventDto == null){
            throw new CustomException(EventErrorCode.EVENT_UPDATE_FAILED);
        }
        this.startTime = eventDto.getStartTime();
        this.endTime = eventDto.getEndTime();
        this.location = eventDto.getLocation();
        this.type = eventDto.getType();
        this.style = eventDto.getStyle();
    }

    public void updateWeather(EventDto eventDto) {
        if (this.weather == null) {
            this.weather = new Weather(eventDto);
        } else {
            this.weather.updateWeather(eventDto);
        }
    }
}
