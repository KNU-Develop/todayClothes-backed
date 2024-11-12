package org.project.todayclothes.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.project.todayclothes.dto.EventReqDto;
import org.project.todayclothes.dto.EventResDto;
import org.project.todayclothes.exception.BusinessException;
import org.project.todayclothes.exception.code.EventErrorCode;
import org.project.todayclothes.exception.code.UserErrorCode;
import org.project.todayclothes.global.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime startTime;
    private String location;
    @Enumerated(EnumType.STRING)
    private Type type;
    @Enumerated(EnumType.STRING)
    private Style style;
    @Enumerated(EnumType.STRING)
    private Gender gender;
    @Enumerated(EnumType.STRING)
    private Timezone timezone;

    private String imagePath="https://todayclothes-file.s3.ap-northeast-2.amazonaws.com/reviews/%ED%99%94%EB%A9%B4+%EC%BA%A1%EC%B2%98+2024-11-04+135744.jpg";
    @ElementCollection
    @CollectionTable(name = "event_images", joinColumns = @JoinColumn(name = "event_id"))
    @Column(name = "my_image_path")
    private List<String> myImgPaths = new ArrayList<>();

    private String comment;
    private LocalDateTime createdAt;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private Review review;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "weather_id")
    private Weather weather;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public Event(EventReqDto eventReqDto, Weather weather, User user) {
        if (eventReqDto == null) {
            throw new BusinessException(EventErrorCode.EVENT_CREATION_FAILED);
        }
        if (user == null) {
            throw new BusinessException(UserErrorCode.USER_NOT_FOUND);
        }
        this.startTime = eventReqDto.getStartTime();
        this.location = eventReqDto.getLocation();
        this.type = eventReqDto.getType();
        this.style = eventReqDto.getStyle();
        this.gender = eventReqDto.getGender();
        this.timezone = eventReqDto.getTimezone();
        this.weather = weather;
        this.user = user;
        this.createdAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public void updateEvent(EventReqDto eventReqDto) {
        if (eventReqDto == null){
            throw new BusinessException(EventErrorCode.EVENT_UPDATE_FAILED);
        }
        this.startTime = eventReqDto.getStartTime();
        this.location = eventReqDto.getLocation();
        this.type = eventReqDto.getType();
        this.style = eventReqDto.getStyle();
    }

    public void updateWeather(EventReqDto eventReqDto) {
        if (this.weather == null) {
            this.weather = new Weather(eventReqDto);
        } else {
            this.weather.updateWeather(eventReqDto);
        }
    }

    public Event updateMyImagePath(List<String> myImgPaths) {
        if (myImgPaths == null || myImgPaths.isEmpty()) {
            throw new BusinessException(EventErrorCode.EVENT_UPDATE_FAILED);
        }
        this.myImgPaths = new ArrayList<>(myImgPaths);
        return this;
    }
    public Event updateImagePath(String imagePath) {
        if (imagePath == null) {
            throw new BusinessException(EventErrorCode.EVENT_UPDATE_FAILED);
        }
        this.imagePath = imagePath;
        return this;
    }
    public Event updateComment(String comment) {
        this.comment = comment;
        return this;
    }

    public Event associateReview(Review review) {
        this.review = review;
        return this;
    }
}
