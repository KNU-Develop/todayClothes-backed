package org.project.todayclothes.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.project.todayclothes.dto.oauth2.CustomOAuth2User;
import org.project.todayclothes.dto.oauth2.OAuth2Response;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String socialId;
    private String name;
    private String email;
    private String role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Event> events;

    public User(CustomOAuth2User customOAuth2User, String email) {
        this.socialId = customOAuth2User.getName();
        this.role = customOAuth2User.getRole();
        this.email = email;
    }

    public User(String socialId, OAuth2Response oAuth2Response) {
        this.socialId = socialId;
        this.name = oAuth2Response.getName();
        this.email = oAuth2Response.getEmail();
        this.role = "USER";
    }

    public void authenticationInfoUpdate(OAuth2Response oAuth2Response) {
        if (oAuth2Response != null) {
            if (oAuth2Response.getEmail() != null && this.email.equals(oAuth2Response.getEmail())) {
                this.email = oAuth2Response.getEmail();
            }
        }
    }
}