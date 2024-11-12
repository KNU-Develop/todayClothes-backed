package org.project.todayclothes.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.project.todayclothes.global.Gender;
import org.project.todayclothes.global.Style;
import org.project.todayclothes.global.Type;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ReqRecommendClotheDto {
    private Weather weather;
    private LocalDateTime time;
    private String location;
    private Type activityType;
    private Gender gender;
    private Style activityStyle;

    public ReqRecommendClotheDto(Weather weather, LocalDateTime time, String location, Type activityType,
                                 Gender gender, Style activityStyle) {
        this.weather = weather;
        this.time = time;
        this.location = location;
        this.activityType = activityType;
        this.gender = gender;
        this.activityStyle = activityStyle;
    }
}

@Getter
@NoArgsConstructor
class Weather {
    private double temperature;
    private double feelsLike;
    private double precipitationChance;
    private double humidity;
    private double windSpeed;

    public Weather(double temperature, double feelsLike, double precipitationChance, double humidity, double windSpeed) {
        this.temperature = temperature;
        this.feelsLike = feelsLike;
        this.precipitationChance = precipitationChance;
        this.humidity = humidity;
        this.windSpeed = windSpeed;
    }
}