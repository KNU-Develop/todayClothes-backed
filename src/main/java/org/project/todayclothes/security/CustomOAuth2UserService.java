package org.project.todayclothes.security;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.project.todayclothes.dto.oauth2.*;
import org.project.todayclothes.entity.User;
import org.project.todayclothes.repository.UserRepository;
import org.project.todayclothes.security.jwt.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final PasswordEncoder passwordEncoder;

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

        Oauth2UserDto oauth2UserDto = new Oauth2UserDto(oAuth2Response, "USER");
        CustomOAuth2User customOAuth2User = new CustomOAuth2User(oauth2UserDto);

//        String encodeSocialId = encodeSocialId(customOAuth2User.getSocialId());
//        String encodeSocialId = customOAuth2User.getSocialId();
        String socialId = customOAuth2User.getSocialId();

        Optional<User> userOpt = userRepository.findBySocialId(socialId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.authenticationInfoUpdate(oAuth2Response);
            userRepository.save(user);
        } else {
            User newUser = new User(socialId, oAuth2Response.getEmail(), customOAuth2User.getRole());
            userRepository.save(newUser);
        }
        return customOAuth2User;
    }

    public String encodeSocialId(String socialId) {
        return passwordEncoder.encode(socialId);
    }
}
