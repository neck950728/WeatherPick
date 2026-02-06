package com.neck.weatherpick.server.dto;

public record WeatherRecommendationResponse(WeatherNowResponse weather, String message) {}