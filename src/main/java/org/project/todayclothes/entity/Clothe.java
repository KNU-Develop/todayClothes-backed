package org.project.todayclothes.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.project.todayclothes.dto.crawling.ClotheDto;
import org.project.todayclothes.global.Category;

@Entity
@Getter
@NoArgsConstructor
public class Clothe {
    @Id @GeneratedValue
    private Long id;
    private String name;
    private Integer price;
    private Category category;
    @Column(length = 65535)
    private String content;
    private String imgUrl;

    public Clothe(ClotheDto clotheDto) {
        this.name = clotheDto.getName();
        this.price = clotheDto.getPrice();
        this.category = clotheDto.getCategory() == Category.BEANIE ? Category.CAP : clotheDto.getCategory();
        this.content = clotheDto.getLink();
        this.imgUrl = clotheDto.getImgUrl();
    }

    @Override
    public String toString() {
        return "Clothe{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", category=" + category +
                ", content='" + content + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                '}';
    }
}
