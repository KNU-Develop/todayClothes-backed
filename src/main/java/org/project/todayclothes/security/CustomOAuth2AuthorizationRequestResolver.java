package org.project.todayclothes.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CustomOAuth2AuthorizationRequestResolver implements OAuth2AuthorizationRequestResolver {
    private final OAuth2AuthorizationRequestResolver defaultResolver;

    @Value("${spring.jwt.default_redirect_uri}")
    private String DEFAULT_REDIRECT_URI;

    @Value("${spring.jwt.allowed_redirect_uri_list}")
    private List<String> ALLOWED_REDIRECT_URI_LIST;

    public CustomOAuth2AuthorizationRequestResolver(ClientRegistrationRepository clientRegistrationRepository) {
        this.defaultResolver = new DefaultOAuth2AuthorizationRequestResolver(clientRegistrationRepository, "/oauth2/authorization");
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
        OAuth2AuthorizationRequest authorizationRequest = defaultResolver.resolve(request);
        registeredRedirectUrl(authorizationRequest, request);
        return authorizationRequest;
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String clientRegistrationId) {
        OAuth2AuthorizationRequest authorizationRequest = defaultResolver.resolve(request, clientRegistrationId);
        registeredRedirectUrl(authorizationRequest, request);
        return authorizationRequest;
    }

    private void registeredRedirectUrl(OAuth2AuthorizationRequest authorizationRequest, HttpServletRequest request) {
        if (authorizationRequest == null) {
            return;
        }

        String redirectUri = request.getParameter("redirect_uri");
        if (redirectUri == null || redirectUri.isEmpty()) {
            redirectUri = DEFAULT_REDIRECT_URI;
        } else if (!isValidRedirectUri(redirectUri)) {
            throw new IllegalArgumentException("Invalid redirect_uri: " + redirectUri);
        }
        request.getSession().setAttribute("redirect_uri", redirectUri);
    }

    private boolean isValidRedirectUri(String redirectUri) {
        return ALLOWED_REDIRECT_URI_LIST.stream().anyMatch(redirectUri::startsWith);
    }
}
