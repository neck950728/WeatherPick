package com.neck.weatherpick.server.controller;

import com.neck.weatherpick.server.dto.WeatherNowResponse;
import com.neck.weatherpick.server.dto.WeatherRecommendationResponse;
import com.neck.weatherpick.server.service.WeatherService;
import com.neck.weatherpick.server.service.OutfitRecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/weather")
@RequiredArgsConstructor
public class WeatherController {
    private final WeatherService weatherService;
    private final OutfitRecommendationService outfitRecommendationService;

    @GetMapping("/now")
    public WeatherRecommendationResponse now(@RequestParam("region") String region) {
        WeatherNowResponse weather = weatherService.getNowByRegion(region);
        String message = outfitRecommendationService.recommend(weather);
        return new WeatherRecommendationResponse(weather, message);
    }
}