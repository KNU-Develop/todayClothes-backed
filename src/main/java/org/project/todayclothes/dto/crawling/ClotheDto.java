package org.project.todayclothes.dto.crawling;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.project.todayclothes.global.Category;

@Getter
public class ClotheDto {
    private String name;
    private Integer price;
    private Category category;
    private String description;
    private String link;
    private String imgUrl;

    @Builder
    public ClotheDto(String name, Integer price, Category category, String description, String link, String imgUrl) {
        this.name = name;
        this.price = price;
        this.category = category;
        this.description = description;
        this.link = link;
        this.imgUrl = imgUrl;
    }

    public void updateDescription(String newDescription) {
        if (newDescription == null || newDescription.isEmpty())
            return;
        this.description = newDescription;
    }
    @Override
    public String toString() { // for test
        return "ClotheDto{" +
                "name='" + name + '\'' +
                ", price=" + price +
                ", category=" + category +
                ", description='" + description + '\'' +
                ", link='" + link + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                '}';
    }
}
