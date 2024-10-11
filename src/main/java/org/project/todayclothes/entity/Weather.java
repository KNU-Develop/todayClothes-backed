package org.project.todayclothes.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.project.todayclothes.dto.EventReqDto;
import org.project.todayclothes.exception.BusinessException;
import org.project.todayclothes.exception.code.EventErrorCode;

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

    public Weather(EventReqDto eventReqDto) {
        this.weather = eventReqDto.getWeather();
        this.temp = eventReqDto.getTemp();
        this.rain = eventReqDto.getRain();
        this.wind = eventReqDto.getWind();
        this.humidity = eventReqDto.getHumidity();
        this.feelsLike = eventReqDto.getFeelsLike();
    }
    public void updateWeather(EventReqDto eventReqDto) {
        if (eventReqDto == null) {
            throw new BusinessException(EventErrorCode.EVENT_UPDATE_FAILED);
        }
        this.weather = eventReqDto.getWeather();
        this.temp = eventReqDto.getTemp();
        this.rain = eventReqDto.getRain();
        this.wind = eventReqDto.getWind();
        this.humidity = eventReqDto.getHumidity();
        this.feelsLike = eventReqDto.getFeelsLike();
    }
}
