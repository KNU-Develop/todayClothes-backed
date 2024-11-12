package org.project.todayclothes.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.project.todayclothes.global.Gender;
import org.project.todayclothes.global.Style;
import org.project.todayclothes.global.Type;

@Getter
@NoArgsConstructor
public class ReqRecommendClotheDto {
    private Weather weather;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private String time;

    private String location;

    @JsonProperty("activity_type")
    private Type activityType;

    private Gender gender;

    @JsonProperty("activity_style")
    private Style activityStyle;

    public ReqRecommendClotheDto(Weather weather, String time, String location, Type activityType,
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
    private int temperature;

    @JsonProperty("feels_like")
    private int feelsLike;

    @JsonProperty("precipitation_chance")
    private int precipitationChance;

    private int humidity;

    @JsonProperty("wind_speed")
    private int windSpeed;

    public Weather(int temperature, int feelsLike, int precipitationChance, int humidity, int windSpeed) {
        this.temperature = temperature;
        this.feelsLike = feelsLike;
        this.precipitationChance = precipitationChance;
        this.humidity = humidity;
        this.windSpeed = windSpeed;
    }
}
