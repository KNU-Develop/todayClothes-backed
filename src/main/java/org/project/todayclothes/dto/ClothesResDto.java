package org.project.todayclothes.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.project.todayclothes.entity.Event;
import org.project.todayclothes.global.Feedback;
import org.project.todayclothes.global.Style;
import org.project.todayclothes.global.Type;

@Getter
@RequiredArgsConstructor
public class ClothesResDto {
    private final Long clothesId;
    private final String imgPath;
    private final String location;
    private final ReviewDto review;
    private final Type type;
    private final Style style;
    private final int weather;
    private final double wind;
    private final double rain;
    private final double humidity;
    private final double feelsLike;
    private final double temp;

    @Getter
    @RequiredArgsConstructor
    public static class ReviewDto {
        private final Feedback feedback;
    }

    public static ClothesResDto from(Event event) {
        return new ClothesResDto(
                event.getId(),
                event.getImagePath(),
                event.getLocation(),
                event.getReview() != null ? new ReviewDto(event.getReview().getFeedback()) : null,
                event.getType(),
                event.getStyle(),
                event.getWeather().getWeather(),
                event.getWeather().getWind(),
                event.getWeather().getRain(),
                event.getWeather().getHumidity(),
                event.getWeather().getFeelsLike(),
                event.getWeather().getTemp()
        );
    }
}
