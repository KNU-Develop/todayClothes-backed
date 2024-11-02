package org.project.todayclothes.entity;

import jakarta.persistence.*;
import lombok.Builder;
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
    private String category;
    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String description;
    private String imgUrl;
    private String infoUrl;
    private String image;

    @Builder
    public Clothe(ClotheDto clotheDto) {
        this.name = clotheDto.getName();
        this.price = clotheDto.getPrice();
        this.category = groupingCategory(clotheDto.getCategory());
        this.description = clotheDto.getDescription();
        this.imgUrl = clotheDto.getImgUrl();
        this.image = clotheDto.getImage();
        this.infoUrl = clotheDto.getLink();
    }

    public void updateImage(String image) {
        this.image = image;
    }

    private String groupingCategory(Category category) {
        return switch (category) {
            case TOP, TOPS_TEE, TOPS_KNIT, TOPS_BLOUSE -> "TOP";
            case PANTS, SKIRTS -> "BOTTOM";
            case OUTER, OUTERS, NEW_WINTER -> "OUTER";
            case SHOES, SKIRT -> "SHOES";
            case BEANIE, CAP, BAGS, SUNGLASSES, DRESSER, JEWELRY, ACC -> "ACC";
        };
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
                ", infoUrl='" + infoUrl + '\'' +
                ", image='" + image + '\'' +
                '}';
    }
}
