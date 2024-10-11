package org.project.todayclothes.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.project.todayclothes.entity.Event;
import org.project.todayclothes.global.Style;
import org.project.todayclothes.global.Type;

@Getter
@NoArgsConstructor
public class EventResDto {

    private String location;
    private String imgPath;
    private String comment;
    private Type type;
    private Style style;

    private Integer weather;
    private double wind;
    private double rain;
    private double humidity;
    private double feelsLike;
    private double temp;

    @Builder
    public EventResDto(String location, String imgPath, String comment, Type type, Style style, Integer weather,
                       double wind, double rain, double humidity, double feelsLike, double temp) {
        this.location = location;
        this.imgPath = imgPath;
        this.comment = comment;
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
