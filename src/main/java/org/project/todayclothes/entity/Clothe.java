package org.project.todayclothes.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import org.project.todayclothes.global.Category;

@Entity
@Getter
public class Clothe {
    @Id @GeneratedValue
    private Long id;
    private String name;
    private Integer price;
    private Category category;
    private String content;
}
