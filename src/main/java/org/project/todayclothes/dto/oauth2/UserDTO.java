package org.project.todayclothes.dto.oauth2;

import lombok.Getter;

@Getter
public class UserDTO {
    private String userId;
    private String name;
    private String role;

    public UserDTO(String userId, String name, String role) {
        this.userId = userId;
        this.name = name;
        this.role = role;
    }

    public UserDTO(String name, String role) {
        this.name = name;
        this.role = role;
    }
}
