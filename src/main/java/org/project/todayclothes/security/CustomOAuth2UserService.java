package org.project.todayclothes.security;

import lombok.RequiredArgsConstructor;
import org.project.todayclothes.dto.oauth2.*;
import org.project.todayclothes.entity.User;
import org.project.todayclothes.repository.UserRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);
//        System.out.println(oauth2User.getAttributes());
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        OAuth2Response oAuth2Response = null;
        if (registrationId.equals("google")) {
            oAuth2Response = new GoogleOauth2Response(oauth2User.getAttributes());
        } else if (registrationId.equals("kakao")) {
            oAuth2Response = new KakaoOauth2Response(oauth2User.getAttributes());
        } else {
            return null;
        }

        CustomOAuth2User customOAuth2User = new CustomOAuth2User(new Oauth2UserDto(oAuth2Response, "USER"));

        Optional<User> userOpt = userRepository.findBySocialId(customOAuth2User.getName());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.authenticationInfoUpdate(oAuth2Response);
            userRepository.save(user);
        } else {
            User newUser = new User(customOAuth2User, oAuth2Response.getEmail());
            userRepository.save(newUser);
        }

        return customOAuth2User;
    }
}
