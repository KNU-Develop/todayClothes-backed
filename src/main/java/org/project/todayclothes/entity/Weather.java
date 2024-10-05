package org.project.todayclothes.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.project.todayclothes.dto.EventDto;

@Entity
@Getter
@NoArgsConstructor
public class Weather {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer weather;
    private double temp;
    private double rain;
    private double wind;
    private double humidity;
    private double feelsLike;

    public Weather(EventDto eventDto) {
        this.weather = eventDto.getWeather();
        this.temp = eventDto.getTemp();
        this.rain = eventDto.getRain();
        this.wind = eventDto.getWind();
        this.humidity = eventDto.getHumidity();
        this.feelsLike = eventDto.getFeelsLike();
    }
    public void updateWeather(EventDto eventDto) {
        this.weather = eventDto.getWeather();
        this.temp = eventDto.getTemp();
        this.rain = eventDto.getRain();
        this.wind = eventDto.getWind();
        this.humidity = eventDto.getHumidity();
        this.feelsLike = eventDto.getFeelsLike();
    }
}
