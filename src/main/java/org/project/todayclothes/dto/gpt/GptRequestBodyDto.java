package org.project.todayclothes.dto.gpt;

import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Getter
public class GptRequestBodyDto {
    private String model;
    private List<HashMap<String, String>> messages;
    public GptRequestBodyDto(String model, String content) {
        this.model = model;
        this.messages = new ArrayList<>();
        this.messages.add(new HashMap<>(){{
            put("role", "user");
            put("content", content);
        }});
    }
}

