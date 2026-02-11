package com.neck.weatherpick.server.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.neck.weatherpick.server.client.ai.OpenAiResponsesClient;
import com.neck.weatherpick.server.dto.WeatherNowResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OutfitRecommendationService {
    private final OpenAiResponsesClient openAi;

    @Cacheable(
            cacheNames = "aiReco",
            key = "T(com.neck.weatherpick.server.cache.CacheKeys).aiKey(#p0)",
            unless = "#result == null"
    )
    public String recommend(WeatherNowResponse weather) {
        String system = """
                너는 제공된 날씨 정보를 기반으로 옷차림 및 준비물을 추천하는 엔진이다.
                (설명/사족 일절 금지)
                
                출력은 반드시 아래 형식을 따른다.
                (2줄로 출력)
                
                - 옷차림 : (최대 4개)
                - 준비물 : (최대 4개)
                
                출력 예시는 다음과 같다.
                - 옷차림 : 패딩, 히트텍, 목도리
                - 준비물 : 우산
                """;

        String user = """
                - 기온(섭씨) : %s
                - 1시간 강수량(mm) : %s
                - 습도(%%) : %s
                - 풍속(m/s) : %s
                - 강수 형태 : %s
                - 하늘 상태 : %s
                """.formatted(
                weather.tempC(),
                weather.precipitation1hMm(),
                weather.humidity(),
                weather.windSpeedMs(),
                weather.precipType(),
                weather.skyType()
        );

        JsonNode json = openAi.createTextResponse(system, user);
        return OpenAiResponsesClient.extractOutputText(json);
    }
}