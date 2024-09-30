package org.project.todayclothes.security;

import lombok.RequiredArgsConstructor;
import org.project.todayclothes.dto.oauth2.*;
import org.project.todayclothes.repository.UserRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
//        System.out.println(oAuth2User);

        String registrationld = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Response oAuth2Response = null;
        if (registrationld.equals("google")) {
            oAuth2Response = new GoogleOauth2Response(oAuth2User.getAttributes());
        } else if (registrationld.equals("kakao")) {
            oAuth2Response = new KakaoOauth2Response(oAuth2User.getAttributes());
        } else {
            return null;
        }
        String userId = oAuth2Response.getProvider() + oAuth2Response.getProviderId();
        UserDTO userDTO = new UserDTO(userId, oAuth2Response.getName(), "USER");
        return new CustomOAuth2User(userDTO);
    }
}
