package org.project.todayclothes.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.project.todayclothes.global.Style;
import org.project.todayclothes.global.Type;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
public class EventDto {

    private String location;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Type type;
    private Style style;

    private Integer weather;
    private double wind;
    private double rain;
    private double humidity;
    private double feelsLike;
    private double temp;

    public EventDto(String location, LocalDateTime startTime, LocalDateTime endTime, Type type, Style style,
                    Integer weather, double wind, double rain, double humidity, double feelsLike, double temp) {
        this.location = location;
        this.startTime = startTime;
        this.endTime = endTime;
        this.type = type;
        this.style = style;
        this.weather = weather;
        this.wind = wind;
        this.rain = rain;
        this.humidity = humidity;
        this.feelsLike = feelsLike;
        this.temp = temp;
    }
}
