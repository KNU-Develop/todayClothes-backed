package org.project.todayclothes.dto.oauth2;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

@Getter
public class CustomOAuth2User implements OAuth2User {
    private final Oauth2UserDto oauth2UserDto;
    private final String role;

    public CustomOAuth2User(Oauth2UserDto oauth2UserDto) {
        this.oauth2UserDto = oauth2UserDto;
        this.role = oauth2UserDto.getRole();
    }

    public Long getUserId() {
        return oauth2UserDto.getUserId();
    }

    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();
        collection.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return role;
            }
        });
        return collection;
    }

    @Override
    public String getName() {
        return this.oauth2UserDto.getName();
    }

    public String getSocialId() {
        return this.oauth2UserDto.getSocialId();
    }
}
