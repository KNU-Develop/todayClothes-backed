package org.project.todayclothes.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.project.todayclothes.global.ActivityType;
import org.project.todayclothes.global.Gender;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ReqRecommendClotheDto {
    private Weather weather;
    private LocalDateTime time;
    private String location;
    private ActivityType activityType;
    private Gender gender;
    private String activityStyle;

    public ReqRecommendClotheDto(Weather weather, LocalDateTime time, String location, ActivityType activityType,
                                 Gender gender, String activityStyle) {
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
}