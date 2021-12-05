package com.markmihalkovich.course.credentials;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@RequiredArgsConstructor
@ConfigurationProperties("credentials")
@Getter
@ConstructorBinding
public class CredentialsProperties {

    private final CloudinaryCredentials cloudinaryCredentials;

    @Getter
    @Setter
    @RequiredArgsConstructor
    public static class CloudinaryCredentials {
        private String cloudName;

        private String apiKey;

        private String apiSecret;
    }
}
