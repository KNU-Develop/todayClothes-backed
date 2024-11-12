package org.project.todayclothes.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.project.todayclothes.global.Gender;
import org.project.todayclothes.global.Style;
import org.project.todayclothes.global.Timezone;
import org.project.todayclothes.global.Type;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class EventReqDto {

    private String location;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startTime;
    private Type type;
    private Style style;
    private Gender gender;
    private Timezone timezone;

    private Integer weather;
    private double wind;
    private double rain;
    private double humidity;
    private double feelsLike;
    private double temp;

    public ReqRecommendClotheDto toRecommendClotheDto() {
        Weather weatherDto = new Weather(temp, feelsLike, rain, humidity, wind);
        return new ReqRecommendClotheDto(
                weatherDto,
                startTime,
                location,
                type,
                gender,
                style
        );
    }

    @Builder
    public EventReqDto(String location, LocalDateTime startTime, Type type, Style style, Gender gender, Timezone timezone,
                       Integer weather, double wind, double rain, double humidity, double feelsLike, double temp) {
        this.location = location;
        this.startTime = startTime;
        this.type = type;
        this.style = style;
        this.gender = gender;
        this.timezone = timezone;
        this.weather = weather;
        this.wind = wind;
        this.rain = rain;
        this.humidity = humidity;
        this.feelsLike = feelsLike;
        this.temp = temp;
    }

}
