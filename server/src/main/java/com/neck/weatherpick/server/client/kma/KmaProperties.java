package com.neck.weatherpick.server.client.kma;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

// @Component
@ConfigurationProperties(prefix = "kma") // Relaxed Binding 지원 : ex) base-url ↔ baseUrl
@Getter
@Setter
public class KmaProperties {
    private String baseUrl;
    private String serviceKey;
}