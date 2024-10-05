package org.project.todayclothes.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.project.todayclothes.entity.Event;
import org.project.todayclothes.entity.Weather;
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

    public EventResDto(Event event) {
        this.location = event.getLocation();
        this.imgPath = event.getImagePath();
        this.comment = event.getComment();
        this.type = event.getType();
        this.style = event.getStyle();

        Weather weather = event.getWeather();
        if (weather != null) {
            this.weather = weather.getWeather();
            this.wind = weather.getWind();
            this.rain = weather.getRain();
            this.humidity = weather.getHumidity();
            this.feelsLike = weather.getFeelsLike();
            this.temp = weather.getTemp();
        }
    }
}
