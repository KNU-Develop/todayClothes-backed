package org.project.todayclothes.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.project.todayclothes.dto.crawling.ClotheDto;
import org.project.todayclothes.global.Category;

@Entity
@Getter
@NoArgsConstructor
@Builder
public class Clothe {
    @Id @GeneratedValue
    private Long id;
    private String name;
    private Integer price;
    private Category category;
    @Column(length = 65535)
    private String description;
    private String imgUrl;
    private String image;

    public Clothe(ClotheDto clotheDto) {
        this.name = clotheDto.getName();
        this.price = clotheDto.getPrice();
//        this.category = clotheDto.getCategory() == Category.BEANIE ? Category.CAP : clotheDto.getCategory();
        this.description = clotheDto.getDescription();
        this.imgUrl = clotheDto.getImgUrl();
    }

    public Clothe(Long id, String name, Integer price, Category category, String description, String imgUrl, String image) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
        this.description = description;
        this.imgUrl = imgUrl;
        this.image = image;
    }

    public void updateImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "Clothe{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", category=" + category +
                ", description='" + description + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                '}';
    }
}
