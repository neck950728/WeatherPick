package com.neck.weatherpick.server.client.geo;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

// @Component
@ConfigurationProperties(prefix = "kakao") // Relaxed Binding 지원 : ex) base-url ↔ baseUrl
@Getter
@Setter
public class KakaoLocalProperties {
    private String baseUrl;
    private String restApiKey;
}