package org.project.todayclothes.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.project.todayclothes.dto.EventReqDto;
import org.project.todayclothes.exception.BusinessException;
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

    public Event(EventReqDto eventReqDto, Weather weather) {
        this.startTime = eventReqDto.getStartTime();
        this.endTime = eventReqDto.getEndTime();
        this.location = eventReqDto.getLocation();
        this.type = eventReqDto.getType();
        this.style = eventReqDto.getStyle();
        this.weather = weather;
    }

    public void updateEvent(EventReqDto eventReqDto) {
        if (eventReqDto == null){
            throw new BusinessException(EventErrorCode.EVENT_UPDATE_FAILED);
        }
        this.startTime = eventReqDto.getStartTime();
        this.endTime = eventReqDto.getEndTime();
        this.location = eventReqDto.getLocation();
        this.type = eventReqDto.getType();
        this.style = eventReqDto.getStyle();
    }

    public void updateWeather(EventReqDto eventReqDto) {
        if (this.weather == null) {
            this.weather = new Weather(eventReqDto);
        } else {
            this.weather.updateWeather(eventReqDto);
        }
    }
}
