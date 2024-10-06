package org.project.todayclothes.dto.crawling;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.project.todayclothes.global.Category;

@Setter
public class ClotheDto {
    private String name;
    private Integer price;
    private Category category;
    private String content;
    private String link;
    private String imgUrl;

    @Builder
    public ClotheDto(String name, Integer price, Category category, String content, String link, String imgUrl) {
        this.name = name;
        this.price = price;
        this.category = category;
        this.content = content;
        this.link = link;
        this.imgUrl = imgUrl;
    }

    @Override
    public String toString() {
        return "ClotheDto{" +
                "name='" + name + '\'' +
                ", price=" + price +
                ", category=" + category +
                ", content='" + content + '\'' +
                ", link='" + link + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                '}';
    }
}
