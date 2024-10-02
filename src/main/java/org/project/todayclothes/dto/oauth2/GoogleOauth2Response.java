package org.project.todayclothes.dto.oauth2;

import java.util.Map;

public class GoogleOauth2Response implements OAuth2Response {
    private final Map<String, Object> attribute;

    public GoogleOauth2Response(Map<String, Object> attribute) {
        this.attribute = attribute;
    }

    @Override
    public String getProvider() {
        return "google";
    }

    @Override
    public String getProviderId() {
        return attribute.get("sub").toString();
    }

    @Override
    public String getEmail() {
        return attribute.get("email").toString();
    }

    @Override
    public String getName() {
        return attribute.get("name").toString();
    }
}
