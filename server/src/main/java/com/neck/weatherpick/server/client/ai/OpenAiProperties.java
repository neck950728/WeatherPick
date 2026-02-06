package com.neck.weatherpick.server.client.ai;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

// @Component
@ConfigurationProperties(prefix = "openai") // Relaxed Binding 지원 : ex) base-url ↔ baseUrl
@Getter
@Setter
public class OpenAiProperties {
    private String baseUrl;
    private String apiKey;
    private String model;
}