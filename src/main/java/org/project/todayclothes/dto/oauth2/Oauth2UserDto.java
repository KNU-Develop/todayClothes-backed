package org.project.todayclothes.dto.oauth2;

import lombok.Getter;

@Getter
public class Oauth2UserDto {
    private String socialId;
    private String name;
    private String role;

    public Oauth2UserDto(OAuth2Response oAuth2Response, String role) {
        this.socialId = oAuth2Response.getProvider() + "#" +oAuth2Response.getProviderId();
        this.name = oAuth2Response.getName();
        this.role = role;
    }
    public Oauth2UserDto(String socialId, String role) {
        this.socialId = socialId;
        this.role = role;
    }
}
